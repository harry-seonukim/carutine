#!/usr/bin/env bash
# 스모크 시나리오: 설치 → 실행 → 화면 텍스트를 찾아 탭 → 스크린샷 → 크래시 검사
set -u
O=smoke-out; mkdir -p $O
PKG=com.carutine.app
n=0
shot() { n=$((n+1)); f=$(printf "%02d_%s.png" $n "$1"); adb exec-out screencap -p > "$O/$f"; adb shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1; adb pull /sdcard/ui.xml "$O/${f%.png}.xml" >/dev/null 2>&1; echo "shot $f"; }
alive() { adb shell pidof $PKG >/dev/null 2>&1; }
# 화면에서 text 또는 content-desc에 $1이 들어간 첫 노드 중심을 탭
tap_text() {
  adb shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1; adb pull /sdcard/ui.xml /tmp/ui.xml >/dev/null 2>&1
  b=$(python3 - "$1" <<'PY'
import sys,re,xml.etree.ElementTree as ET
t=sys.argv[1]
try: root=ET.parse('/tmp/ui.xml').getroot()
except Exception: sys.exit(0)
for n in root.iter('node'):
    if t in (n.get('text') or '') or t in (n.get('content-desc') or ''):
        m=re.findall(r'\d+',n.get('bounds','')); 
        if len(m)==4:
            x1,y1,x2,y2=map(int,m); print((x1+x2)//2,(y1+y2)//2); break
PY
)
  if [ -n "$b" ]; then adb shell input tap $b; echo "tap '$1' at $b"; sleep 2; return 0; else echo "NOT FOUND: '$1'" | tee -a $O/missing.txt; return 1; fi
}
adb logcat -c
adb install -r app.apk 2>&1 | tee $O/install.txt
adb shell pm grant $PKG android.permission.RECORD_AUDIO 2>/dev/null || true
adb shell am start -W -n $PKG/.MainActivity | tee $O/start.txt
sleep 6; shot launch
alive || echo "CRASH after launch" | tee -a $O/result.txt
# 권한 대화상자가 뜨면 닫기
tap_text "허용" || tap_text "Allow" || true
for step in "갤러리" "퇴근길 겨울"; do tap_text "$step" && shot "$step"; done
tap_text "가져오기" && shot "import_sheet"
tap_text "가상 위치" && shot "place"
tap_text "가져오기" ; tap_text "완료" ; tap_text "저장"; shot "after_import"
tap_text "홈" || tap_text "내 루틴"; shot "home"
tap_text "실행" && sleep 2 && shot "run_result"
tap_text "원래대로" && shot "undo"
tap_text "추움" ; tap_text "회사 출발" && sleep 3 && shot "auto_run"
tap_text "직접 만들기" && shot "editor"
adb shell input keyevent KEYCODE_BACK; sleep 1
tap_text "설정" && shot "settings"
# 화면 회전(세로)
adb shell settings put system accelerometer_rotation 0; adb shell settings put system user_rotation 1; sleep 3; shot "rotated"
alive && echo "ALIVE at end" | tee -a $O/result.txt || echo "CRASH at end" | tee -a $O/result.txt
adb logcat -d > $O/logcat.txt
grep -E "FATAL EXCEPTION|AndroidRuntime" -A 30 $O/logcat.txt > $O/crash.txt || true
grep -iE "carutine|Carutine" $O/logcat.txt | tail -200 > $O/app-log.txt || true
if [ -s $O/crash.txt ]; then echo "FATAL found"; fi
exit 0
