# DP-RandomChallenge

플레이어마다 **개인 랜덤 챌린지(미션)** 를 부여하고, 완료하면 보상을 지급하는 플러그인입니다.
챌린지 제작부터 보상 설정까지 **전부 게임 내 GUI로** 관리합니다. config 파일을 직접 수정할 필요가 없습니다.

- 지원 버전: Minecraft 1.21+ (Spigot / Paper)
- 필수 선행 플러그인: [DPP-Core](https://github.com/darksoldier1404) 5.4 이상
- 선택 연동: PlaceholderAPI (플레이스홀더), EssentialsX (돈 보상)

---

## 목차

1. [설치](#1-설치)
2. [작동 방식](#2-작동-방식)
3. [커맨드 & 권한](#3-커맨드--권한)
4. [챌린지 만들기 (GUI)](#4-챌린지-만들기-gui)
5. [챌린지 편집 GUI](#5-챌린지-편집-gui)
6. [보상 설정](#6-보상-설정)
7. [챌린지 타입 전체 목록](#7-챌린지-타입-전체-목록)
8. [config.yml](#8-configyml)
9. [PlaceholderAPI](#9-placeholderapi)
10. [데이터 저장 위치](#10-데이터-저장-위치)
11. [자주 묻는 질문](#11-자주-묻는-질문)
12. [개발자 API](#12-개발자-api)

---

## 1. 설치

1. `DPP-Core.jar`와 `DP-RandomChallenge.jar`를 서버의 `plugins/` 폴더에 넣습니다.
2. 서버를 재시작합니다.
3. 게임에 접속해 `/rc create <챌린지ID>` 로 첫 챌린지를 만듭니다.

> 처음 설치하면 챌린지가 0개입니다. 챌린지가 하나도 없으면 플레이어에게 아무것도 배정되지 않으니,
> 먼저 챌린지를 만들어 주세요.

---

## 2. 작동 방식

```
플레이어 접속
  → 챌린지가 없거나 만료됨? → 랜덤 챌린지 자동 배정
  → 플레이가 진행도에 반영됨 (몹 처치, 채광, 이동 등)
  → 목표 달성 → 보상 지급 → 갱신 모드에 따라 다음 챌린지 부여
```

- 챌린지는 **플레이어마다 독립적**입니다. 같은 시간에 서로 다른 챌린지를 진행합니다.
- 진행도는 자동 저장되며 서버를 재시작해도 유지됩니다.

### 갱신 모드 (config.yml의 `renewal-mode`)

| 모드 | 동작 |
|------|------|
| `TIME` | 일정 시간(`time-interval`)마다 새 챌린지로 교체. 완료해도 시간이 될 때까지 대기 |
| `COMPLETE` | 완료하는 즉시 다음 챌린지 부여. 시간 제한 없음 |
| `MIXED` (기본값) | 완료하면 즉시 다음 챌린지, 완료 못 하면 시간 초과 시 교체 |
| `FIXED` | **자동 배정/갱신 없음.** 관리자나 시스템이 `/rc admin give` 커맨드로만 미션을 부여하고, 완료 후에는 다음 부여 전까지 대기. 이벤트 미션, 퀘스트 시스템 연동, 단계별 미션 등에 활용 |

> `FIXED` 모드 활용 예시: 콘솔/커맨드 블록/다른 플러그인에서
> `rc admin give Steve zombie_hunt` 처럼 실행하면 특정 미션을 특정 플레이어에게 정확히 지정해 줄 수 있습니다.

---

## 3. 커맨드 & 권한

메인 커맨드: `/dprandomchallenge` (별칭: `/randomchallenge`, `/rc`, `/challenge`)

| 커맨드 | 설명 | 권한 |
|--------|------|------|
| `/rc` | 현재 내 챌린지를 채팅으로 확인 | 없음 |
| `/rc info` | 위와 동일 | 없음 |
| `/rc gui` | 내 챌린지 현황 GUI 열기 (진행도, 남은 시간, 보상, 최근 완료 이력) | 없음 |
| `/rc create <ID>` | **새 챌린지 생성** → 타입 선택 GUI로 이어짐 | `dprdch.admin` |
| `/rc admin` | 관리자 GUI 열기 (챌린지 목록/편집/삭제) | `dprdch.admin` |
| `/rc admin give <플레이어>` | 해당 플레이어에게 새 랜덤 챌린지 강제 부여 | `dprdch.admin` |
| `/rc admin give <플레이어> <챌린지ID>` | **특정 챌린지를 지정해서** 부여 (탭완성 지원, 콘솔 사용 가능) | `dprdch.admin` |
| `/rc admin clear <플레이어>` | 해당 플레이어의 현재 챌린지 제거 (무챌린지 상태로) | `dprdch.admin` |
| `/rc admin reset <플레이어>` | 해당 플레이어의 챌린지 데이터 초기화 후 재배정 (FIXED 모드는 재배정 없음) | `dprdch.admin` |
| `/rc reload` | 설정(config.yml) 리로드 | `dprdch.admin` |

- `dprdch.admin` 권한은 기본적으로 OP에게 부여됩니다.

---

## 4. 챌린지 만들기 (GUI)

### 방법 1 — 커맨드로 생성 (권장)

```
/rc create 좀비사냥꾼
```

1. **타입 선택 GUI**가 열립니다. 43가지 챌린지 타입이 아이콘으로 표시됩니다.
2. 원하는 타입을 클릭하면 챌린지가 생성되고 **편집 GUI**가 바로 열립니다.
3. 생성 직후에도 필수 값들이 기본값으로 채워져 있어 그대로 사용 가능합니다.

> 챌린지 ID는 영문/숫자/`_`/`-` 만 사용할 수 있습니다. (최대 32자)

### 방법 2 — 관리자 GUI에서 생성

1. `/rc admin` 으로 챌린지 목록을 엽니다.
2. 하단 도구 줄 왼쪽의 **⭐ 새 챌린지 생성** 버튼을 클릭합니다.
3. 모루 입력창에 챌린지 ID를 입력하고 초록 양털(확인)을 클릭합니다.
4. 이후 타입 선택 → 편집 GUI 흐름은 방법 1과 동일합니다.

### 관리자 목록 GUI 조작법

| 조작 | 동작 |
|------|------|
| 챌린지 좌클릭 | 편집 GUI 열기 |
| 챌린지 Shift+우클릭 | 챌린지 삭제 |

---

## 5. 챌린지 편집 GUI

편집 GUI에서 챌린지의 모든 것을 설정합니다. 변경 사항은 **즉시 저장**됩니다 (리로드 불필요).

```
┌─────────────────────────────────────┐
│              [챌린지 정보]            │  ← ID, 타입, 표시 이름
│                                     │
│   [파라미터] [파라미터] [파라미터] ...   │  ← 타입별 설정값 (표지판 아이콘)
│                                     │
│  [표시이름] [돈보상] [아이템보상] [커맨드] │
│                                     │
│ [« 뒤로]                      [삭제] │
└─────────────────────────────────────┘
```

### 파라미터 버튼 (표지판)

- **참나무 표지판 = 필수 값** (목표 수량, 대상 몹 등) / **자작나무 표지판 = 선택 필터** (월드 제한, 무기 제한 등)
- 클릭하면 모루 입력창이 열립니다. 값을 입력하고 확인을 누르면 적용됩니다.
- 잘못된 값(없는 몹 이름, 숫자가 아닌 값 등)은 자동으로 거부됩니다.
- **선택 파라미터는 Shift+우클릭으로 제거**할 수 있습니다. 제거하면 해당 필터를 적용하지 않습니다.
  - 예: `KILL_MOB`의 `weapon`을 제거하면 아무 무기로나 처치해도 인정됩니다.

### 모루 입력창 사용법

- 이름 입력칸에 값을 입력 → 오른쪽 **초록 양털(확인)** 클릭 → 적용
- 그냥 ESC로 닫으면 변경 없이 취소됩니다.

---

## 6. 보상 설정

보상은 세 종류이며 자유롭게 조합할 수 있습니다. 모두 편집 GUI에서 설정합니다.

### 💰 돈 보상 (금괴 버튼)

- 클릭 → 모루에 금액 입력 → 확인
- EssentialsX가 설치되어 있어야 실제로 지급됩니다.

### 🎁 아이템 보상 (상자 버튼)

1. 상자 버튼을 클릭하면 27칸 보상 아이템 창이 열립니다.
2. **지급할 아이템을 실제로 넣습니다.** 인챈트, 커스텀 이름, NBT가 그대로 보존됩니다.
3. 창을 닫으면 자동 저장되고 편집 GUI로 돌아옵니다.
4. 다시 열면 넣어둔 아이템을 꺼내 회수할 수도 있습니다.

> 보상 지급 시 플레이어 인벤토리가 가득 차면 남은 아이템은 발밑에 드롭됩니다.

### ⌨ 커맨드 보상 (커맨드 블록 버튼)

- **클릭**: 모루에 커맨드 입력 (앞의 `/` 는 생략 가능, `%player%` 가 플레이어 이름으로 치환됨)
  - 예: `give %player% diamond 3`, `lp user %player% permission set some.perm`
- **Shift+우클릭**: 등록된 커맨드 전체 삭제
- 커맨드는 콘솔 권한으로 실행됩니다.

---

## 7. 챌린지 타입 전체 목록

**굵게** = 필수 값, *기울임* = 선택 필터

### ⚔ 전투

| 타입 | 설명 | 설정값 |
|------|------|--------|
| `KILL_MOB` | 몹 처치 | **mob, amount**, *weapon, biome, world* |
| `KILL_PLAYER` | 플레이어 처치 | **amount**, *world, biome* |
| `DAMAGE_DEALT` | 누적 피해량 달성 | **amount**, *world* |
| `KILL_BOSS` | 보스 처치 | **boss**(WITHER/ENDER_DRAGON/ELDER_GUARDIAN)**, amount** |
| `KILL_WITH_BOW` | 활로 처치 | **amount**, *mob, world* |
| `KILL_WHILE_BURNING` | 자신이 불타는 상태로 처치 | **amount**, *mob, world* |
| `DAMAGE_WITH_POTION` | 투척 포션으로 피해 | **amount**, *potion_type, world* |
| `KILL_FROM_DISTANCE` | 원거리에서 처치 (저격) | **amount, distance**, *mob, world* |

### 📦 수집

| 타입 | 설명 | 설정값 |
|------|------|--------|
| `COLLECT_ITEM` | 아이템 수집(줍기) | **item, amount**, *source*(MINE/LOOT/ANY) |
| `CRAFT_ITEM` | 아이템 제작 | **item, amount** |
| `FISH_ITEM` | 낚시 | **item, amount** |
| `SMELT_ITEM` | 화로에서 꺼내기 | **item, amount** |
| `BREW_POTION` | 포션 양조 | **potion_type, amount** |
| `ENCHANT_ITEM` | 인챈트 | **amount**, *item, enchantment* |
| `TRADE_VILLAGER` | 주민 거래 | **amount**, *item* |
| `SHEAR_SHEEP` | 양털 깎기 | **amount**, *color* |
| `EAT_FOOD` | 음식 먹기 | **amount**, *item* |
| `HARVEST_CROP` | 다 자란 작물 수확 | **amount**, *block, world* |
| `TAME_ANIMAL` | 동물 길들이기 | **amount**, *entity* |
| `BREED_ANIMAL` | 동물 번식 | **amount**, *entity* |
| `FILL_BUCKET` | 양동이 채우기 | **amount**, *liquid*(WATER/LAVA) |
| `BONE_MEAL` | 뼛가루 사용 | **amount**, *block* |
| `THROW_PROJECTILE` | 투사체 던지기 | **amount**, *projectile*(SNOWBALL/ENDER_PEARL 등) |

### 🧭 탐험

| 타입 | 설명 | 설정값 |
|------|------|--------|
| `TRAVEL_DISTANCE` | 거리 이동 (블록) | **distance**, *world, mode*(WALK/SPRINT/ANY) |
| `VISIT_BIOME` | 특정 바이옴 방문 | **biome**, *world* |
| `ENTER_STRUCTURE` | 구조물 진입 | **structure**(Village, Fortress 등), *world* |
| `REACH_Y_LEVEL` | 특정 높이 도달 | **y, direction**(ABOVE/BELOW), *world* |
| `RIDE_ENTITY` | 탈것 탑승 유지 (초) | **entity, duration**, *world* |
| `ENTER_PORTAL` | 포탈 통과 | **portal_type**(NETHER/END/ANY)**, amount** |
| `GLIDE_DISTANCE` | 겉날개 활공 거리 (블록) | **distance**, *world* |
| `VISIT_WORLD` | 특정 월드 방문 | **world** |
| `SLEEP_IN_BED` | 침대에서 자기 | **amount** |

### ❤ 생존

| 타입 | 설명 | 설정값 |
|------|------|--------|
| `SURVIVE_TIME` | 생존 시간 (초) | **duration**, *world* |
| `KEEP_HUNGER` | 배고픔 수치 유지 (초) | **level, duration** |
| `NO_DAMAGE_TIME` | 무피해 유지 (초) — 피해 시 초기화 | **duration** |
| `SURVIVE_LOW_HEALTH` | 낮은 체력으로 버티기 (초) | **health, duration**, *world* |
| `SURVIVE_UNDERWATER` | 수중에서 버티기 (초) | **duration**, *world* |
| `WEAR_FULL_ARMOR` | 풀 방어구 착용 유지 (초) | **duration**, *material*(DIAMOND 등) |
| `TAKE_DAMAGE` | 받은 피해 누적 | **amount**, *world* |
| `GAIN_XP` | 경험치 획득 누적 | **amount** |
| `REACH_LEVEL` | 경험치 레벨 도달 | **level** |

> 생존형 챌린지는 조건을 벗어나면 진행도가 0으로 초기화됩니다. (`SURVIVE_TIME`, `RIDE_ENTITY` 제외)

### 🧱 건축

| 타입 | 설명 | 설정값 |
|------|------|--------|
| `PLACE_BLOCKS` | 블록 설치 | **block, amount**, *world* |
| `BREAK_BLOCKS` | 블록 파괴 | **block, amount**, *world* |

---

## 8. config.yml

config.yml에는 **플러그인 설정만** 있습니다. 챌린지는 GUI로 관리하므로 여기에 없습니다.

```yaml
Settings:
  prefix: "&f[ &6DP-RandomChallenge &f] "   # 메시지 접두사
  Lang: en_US                                # 언어 (en_US / ko_KR)
  renewal-mode: MIXED                        # 갱신 모드 (TIME / COMPLETE / MIXED / FIXED)
  time-interval: 1800                        # 갱신 주기 (초) — TIME / MIXED 모드에서 사용
  progress-actionbar: true                   # 진행도 변동 시 액션바 표시 여부
```

- 한국어 메시지를 쓰려면 `Lang: ko_KR` 로 변경 후 `/rc reload`
- `time-interval: 1800` = 30분마다 챌린지 갱신

---

## 9. PlaceholderAPI

PlaceholderAPI가 설치되어 있으면 아래 플레이스홀더를 스코어보드, 채팅, 탭리스트 등에 사용할 수 있습니다.

| 플레이스홀더 | 표시 내용 |
|--------------|-----------|
| `%dpc_challenge_name%` | 현재 챌린지 이름 |
| `%dpc_challenge_objective%` | 현재 챌린지 목표 설명 (예: `좀비 10마리 처치`) |
| `%dpc_challenge_progress%` | 현재 진행도 |
| `%dpc_challenge_target%` | 목표 수치 |
| `%dpc_challenge_percent%` | 진행률 (예: `30%`) |
| `%dpc_challenge_timeleft%` | 다음 갱신까지 남은 시간 (예: `23:14`) |
| `%dpc_challenge_completed%` | 지금까지 완료한 챌린지 총 개수 |

---

## 10. 데이터 저장 위치

```
plugins/DP-RandomChallenge/
├── config.yml              # 플러그인 설정
├── lang/                   # 언어 파일 (en_US.yml, ko_KR.yml)
└── data/
    ├── challenges/         # 챌린지 정의 (챌린지 1개 = 파일 1개)
    └── users/              # 플레이어별 진행 데이터 (UUID.yml)
```

- 챌린지를 다른 서버로 옮기려면 `data/challenges/` 폴더를 복사하면 됩니다.
- 구버전(config.yml에 챌린지를 작성하던 방식)에서 업데이트하면 기존 챌린지가 **자동으로 변환**됩니다.

---

## 11. 자주 묻는 질문

**Q. 챌린지를 만들었는데 플레이어에게 배정되지 않아요.**
이미 접속 중인 플레이어는 다음 갱신 시점(또는 재접속) 에 배정됩니다. 즉시 주고 싶으면 `/rc admin give <플레이어>` 를 사용하세요.
단, `renewal-mode: FIXED` 에서는 자동 배정이 없으므로 항상 커맨드로 부여해야 합니다.

**Q. 진행 중인 챌린지를 삭제하면 어떻게 되나요?**
해당 챌린지를 진행하던 플레이어는 약 1초 내에 자동으로 다른 챌린지를 재배정받습니다.

**Q. 돈 보상이 지급되지 않아요.**
EssentialsX가 설치되어 있는지 확인하세요. 미설치 시 돈 보상은 조용히 건너뜁니다 (아이템/커맨드 보상은 정상 지급).

**Q. 모루 입력창에서 글자를 입력해도 반응이 없어요.**
모루 텍스트 입력은 **MC 1.21 이상**에서만 동작합니다. 서버 버전을 확인하세요.

**Q. ENTER_STRUCTURE의 structure 이름은 어디서 확인하나요?**
Bukkit StructureType 이름을 사용합니다. 예: `Village`, `Fortress`, `Mineshaft`, `Stronghold`, `Monument`, `Mansion`, `Buried_Treasure` 등. 판정은 10초 간격으로 구조물 중심 기준 64블록 이내 접근 시 완료 처리됩니다.

**Q. 같은 챌린지가 연속으로 나올 수 있나요?**
챌린지가 2개 이상이면 직전 챌린지는 제외하고 뽑습니다.

---

## 12. 개발자 API

다른 플러그인에서 DP-RandomChallenge와 연동할 수 있는 전용 API를 제공합니다.

### 의존성 설정

```groovy
// build.gradle
dependencies {
    compileOnly files("libs/DP-RandomChallenge-1.0.0-SNAPSHOT.jar")
}
```

```yaml
# plugin.yml — 필수 연동이면 depend, 선택 연동이면 softdepend
depend: [DP-RandomChallenge]
# 또는
softdepend: [DP-RandomChallenge]
```

### 진입점 — `RandomChallengeAPI`

`com.darksoldier1404.dprdch.api.RandomChallengeAPI` 정적 메서드로 모든 기능에 접근합니다.
**모든 메서드는 메인 스레드에서 호출**해야 하며, softdepend라면 호출 전 `isAvailable()`을 확인하세요.

```java
import com.darksoldier1404.dprdch.api.RandomChallengeAPI;

if (!RandomChallengeAPI.isAvailable()) return;
```

#### 플레이어 상태 조회

```java
ChallengeDefinition def = RandomChallengeAPI.getActiveChallenge(player); // 진행 중 챌린지 (없으면 null)
String id      = RandomChallengeAPI.getCurrentChallengeId(uuid);  // 오프라인 플레이어도 가능
int progress   = RandomChallengeAPI.getProgress(uuid);
int target     = RandomChallengeAPI.getTarget(uuid);
int percent    = RandomChallengeAPI.getProgressPercent(uuid);     // 0~100
boolean done   = RandomChallengeAPI.isCompleted(uuid);            // 완료 후 갱신 대기 중인지
int total      = RandomChallengeAPI.getTotalCompleted(uuid);      // 누적 완료 횟수
List<ChallengeHistoryEntry> history = RandomChallengeAPI.getHistory(uuid); // 최신순 최대 20건
String objective = RandomChallengeAPI.getObjective(def);          // "좀비 10마리 처치" 등
```

#### 플레이어 제어 (FIXED 모드 시스템 연동의 핵심)

```java
RandomChallengeAPI.assignChallenge(player, "zombie_hunt"); // 특정 챌린지 부여 → boolean
RandomChallengeAPI.assignRandom(player);                   // 랜덤 부여 → boolean
RandomChallengeAPI.clearChallenge(player);                 // 현재 챌린지 제거
RandomChallengeAPI.addProgress(player, 5);                 // 진행도 +5 (이벤트 발생, 완료 처리 포함)
RandomChallengeAPI.setProgress(player, 0);                 // 진행도 직접 설정 (이벤트 미발생)
RandomChallengeAPI.completeChallenge(player);              // 즉시 완료 (보상 지급)
```

#### 챌린지 정의 조회 / 관리

```java
Set<String> ids = RandomChallengeAPI.getChallengeIds();
boolean exists  = RandomChallengeAPI.exists("zombie_hunt");

// 코드로 챌린지 생성 — 필수 파라미터는 기본값으로 채워짐
ChallengeDefinition def = RandomChallengeAPI.createChallenge("api_quest", ChallengeType.KILL_MOB);
if (def != null) {
    def.setDisplay("&c좀비 토벌 의뢰");
    def.getParams().put("mob", "ZOMBIE");
    def.getParams().put("amount", 20);
    def.getReward().setMoney(5000);
    RandomChallengeAPI.saveChallenge(def);   // 수정 후 반드시 저장
}

RandomChallengeAPI.deleteChallenge("api_quest");

RenewalMode mode = RandomChallengeAPI.getRenewalMode(); // TIME / COMPLETE / MIXED / FIXED
```

### 커스텀 이벤트 — `com.darksoldier1404.dprdch.api.events`

일반 Bukkit 리스너로 구독합니다. 모두 메인 스레드에서 호출됩니다.

| 이벤트 | 시점 | 취소 | 비고 |
|--------|------|:---:|------|
| `ChallengeAssignEvent` | 챌린지 부여 직전 | ✔ | `setDefinition()`으로 다른 챌린지로 교체 가능, `getCause()`로 부여 원인 확인 |
| `ChallengeProgressEvent` | 진행도 적립 직전 | ✔ | `setAmount()`로 적립량 조정 (0 이하 = 미적립) |
| `ChallengeCompleteEvent` | 완료 직후 (보상 지급 후) | ✘ | `getTotalCompleted()` 포함. 막으려면 ProgressEvent를 취소 |

부여 원인(`AssignCause`): `JOIN`(접속) / `RENEWAL`(시간 갱신) / `COMPLETE`(완료 후 자동 갱신) / `COMMAND`(관리자 커맨드) / `API`(외부 플러그인)

```java
public class MyListener implements Listener {

    // 예: 특정 월드에서는 챌린지 부여 차단
    @EventHandler
    public void onAssign(ChallengeAssignEvent e) {
        if (e.getPlayer().getWorld().getName().equals("lobby")) {
            e.setCancelled(true);
        }
    }

    // 예: VIP는 진행도 2배
    @EventHandler
    public void onProgress(ChallengeProgressEvent e) {
        if (e.getPlayer().hasPermission("myserver.vip")) {
            e.setAmount(e.getAmount() * 2);
        }
    }

    // 예: 완료 시 외부 시스템에 포인트 적립
    @EventHandler
    public void onComplete(ChallengeCompleteEvent e) {
        MyPointSystem.add(e.getPlayer(), 10);
        // FIXED 모드 연동 — 완료하면 다음 미션을 직접 부여
        // RandomChallengeAPI.assignChallenge(e.getPlayer(), "next_mission");
    }
}
```

> **주의 (ChallengeProgressEvent 빈도)**: 생존형 챌린지(SURVIVE_TIME 등)는 매초, 이동형 챌린지는 이동할 때마다 이벤트가 발생합니다. 핸들러는 가볍게 유지하세요.

---

## 빌드 (개발자용)

```bash
./gradlew build    # 결과물: jars/DP-RandomChallenge-x.x.x-SNAPSHOT.jar
```

- 의존성: `com.darksoldier1404.dppc:DPP-Core:5.4-SNAPSHOT` (compileOnly)
- 제작: DEAD_POOLIO_ (darksoldier1029@gmail.com)
