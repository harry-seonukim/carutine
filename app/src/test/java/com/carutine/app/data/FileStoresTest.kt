package com.carutine.app.data

import com.carutine.engine.DeviceKey
import com.carutine.engine.DeviceValue
import com.carutine.engine.Place
import com.carutine.engine.RoutineEngine
import com.carutine.engine.RoutineSource
import com.carutine.engine.RunSource
import com.carutine.engine.StoredRoutine
import com.carutine.engine.UndoSnapshot
import com.carutine.model.Action
import com.carutine.model.AmbientBrightness
import com.carutine.model.AmbientColor
import com.carutine.model.Cap
import com.carutine.model.HvacTempMode
import com.carutine.model.PlaceKey
import com.carutine.model.RoutineJson
import com.carutine.model.Seat
import com.carutine.vehicle.fake.FakeVehicleGateway
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.time.Instant

class FileStoresTest {

    private fun tmpDir(): File = Files.createTempDirectory("carutine-test").toFile()

    private fun seedFile(): File = listOf(File("src/main/assets/seed_routines.json"), File("app/src/main/assets/seed_routines.json"))
        .first { it.isFile }

    @Test fun `시드 20개를 저장했다가 다시 읽으면 같은 루틴이다`() = runBlocking {
        val dir = tmpDir()
        val (specs, bad) = RoutineJson.parseList(seedFile().readText())
        assertTrue(bad.isEmpty())
        val store = FileRoutineStore(dir)
        specs.forEachIndexed { i, s ->
            store.upsert(StoredRoutine("loc_$i", s, enabled = i % 2 == 0, priority = i, source = RoutineSource.IMPORTED, remoteId = s.id))
        }
        val reread = FileRoutineStore(dir).all()
        assertEquals(20, reread.size)
        reread.forEachIndexed { i, r ->
            assertEquals(specs[i], r.spec)
            assertEquals(i % 2 == 0, r.enabled)
            assertEquals(i, r.priority)
            assertEquals(RoutineSource.IMPORTED, r.source)
        }
    }

    @Test fun `우선순위 이동과 삭제가 파일에 반영된다`() = runBlocking {
        val dir = tmpDir()
        val (specs, _) = RoutineJson.parseList(seedFile().readText())
        val store = FileRoutineStore(dir)
        (0 until 3).forEach { store.upsert(StoredRoutine("r$it", specs[it], true, it, RoutineSource.LOCAL)) }
        store.move("r2", -1)
        assertEquals(listOf("r0", "r2", "r1"), FileRoutineStore(dir).all().map { it.localId })
        store.delete("r0")
        assertEquals(listOf("r2", "r1"), FileRoutineStore(dir).all().map { it.localId })
        assertEquals(listOf(0, 1), FileRoutineStore(dir).all().map { it.priority })
    }

    @Test fun `장소·되돌리기·재실행 방지 기록이 다시 읽힌다`() = runBlocking {
        val dir = tmpDir()
        FilePlaceStore(dir).put(listOf(Place(PlaceKey.HOME, "집", 0.0, 0.0), Place(PlaceKey.WORK, "회사", 0.0, 0.1)))
        val places = FilePlaceStore(dir).all()
        assertEquals(setOf(PlaceKey.HOME, PlaceKey.WORK), places.keys)
        assertEquals(0.1, places.getValue(PlaceKey.WORK).lng, 1e-9)

        val at = Instant.ofEpochMilli(1_700_000_000_000)
        val snap = UndoSnapshot(
            at, listOf("r1"),
            listOf(
                UndoSnapshot.Entry(DeviceKey.HvacTemp, DeviceValue.Temperature(22.5), Action.HvacTemp(HvacTempMode.SET, 24.0)),
                UndoSnapshot.Entry(DeviceKey.SeatHeater(Seat.DRIVER), DeviceValue.Level(0), Action.SeatHeater(Seat.DRIVER, 2)),
                UndoSnapshot.Entry(
                    DeviceKey.AmbientLight,
                    DeviceValue.Ambient(false, AmbientColor.COOL, AmbientBrightness.DEFAULT),
                    Action.AmbientLight(true, AmbientColor.WARM, null),
                ),
            ),
        )
        FileUndoStore(dir).save(snap)
        assertEquals(snap, FileUndoStore(dir).load())
        FileUndoStore(dir).clear()
        assertNull(FileUndoStore(dir).load())

        FileCooldownStore(dir).markAutoRun("r1", at)
        assertEquals(at, FileCooldownStore(dir).lastAutoRun("r1"))
        assertNull(FileCooldownStore(dir).lastAutoRun("r2"))
    }

    @Test fun `깨진 파일은 빈 값으로 읽고 앱을 멈추지 않는다`() = runBlocking {
        val dir = tmpDir()
        File(dir, "routines.json").writeText("{ not json")
        assertTrue(FileRoutineStore(dir).all().isEmpty())
    }

    @Test fun `파일 저장소로 엔진을 돌리면 되돌리기가 프로세스 재시작 뒤에도 된다`() = runBlocking {
        val dir = tmpDir()
        val (specs, _) = RoutineJson.parseList(seedFile().readText())
        val manual = specs.first { it.trigger == null }
        FileRoutineStore(dir).upsert(StoredRoutine("m1", manual, true, 0, RoutineSource.IMPORTED))
        val gw = FakeVehicleGateway(Cap.entries.toSet())
        val before = gw.hvacTemp
        fun engine() = RoutineEngine(gw, FileRoutineStore(dir), FilePlaceStore(dir), FileUndoStore(dir), FileCooldownStore(dir), FileRunLog(dir))
        val report = engine().runManually("m1", RunSource.BUTTON)
        assertNotNull(report)
        assertTrue(report!!.undoable)
        // 새 엔진(앱 재시작 가정)에서도 되돌릴 수 있다
        val e2 = engine()
        assertTrue(e2.canUndo())
        e2.undo()
        assertEquals(before, gw.hvacTemp, 1e-9)
        assertFalse(e2.canUndo())
        assertEquals(1, FileRunLog(dir).count())
    }
}
