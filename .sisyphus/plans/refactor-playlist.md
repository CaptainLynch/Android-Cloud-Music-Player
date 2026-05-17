# 重构计划：歌单功能 + 心动模式

## 概述

本次重构包含两部分：
1. **移除原生加密直连功能**（F15 回退）— 删除所有 Direct Mode 相关代码，仅保留代理模式
2. **新增心动模式/智能播放** — 接入 NeteaseCloudMusicApiEnhanced 的 `/playmode/intelligence/list` 接口

---

## Part A: 移除直连模式

### TASK-01: 删除直连模式相关文件（15个）

**操作**: 直接删除以下文件

**加密模块（3个）**:
- `app/src/main/java/com/lynchlin/music/network/crypto/CryptoUtils.kt`
- `app/src/main/java/com/lynchlin/music/network/crypto/LinuxApiCrypto.kt`
- `app/src/main/java/com/lynchlin/music/network/crypto/WeApiCrypto.kt`

**直连网络模块（4个）**:
- `app/src/main/java/com/lynchlin/music/network/NeteaseApiMode.kt`
- `app/src/main/java/com/lynchlin/music/network/NeteaseCookieJar.kt`
- `app/src/main/java/com/lynchlin/music/network/NeteaseDirectInterceptor.kt`
- `app/src/main/java/com/lynchlin/music/network/NeteaseOriginApiService.kt`

**单元测试（8个）**:
- `app/src/test/java/com/lynchlin/music/network/crypto/CryptoIntegrationTest.kt`
- `app/src/test/java/com/lynchlin/music/network/crypto/CryptoUtilsTest.kt`
- `app/src/test/java/com/lynchlin/music/network/crypto/LinuxApiCryptoTest.kt`
- `app/src/test/java/com/lynchlin/music/network/crypto/WeApiCryptoTest.kt`
- `app/src/test/java/com/lynchlin/music/network/NeteaseApiModeTest.kt`
- `app/src/test/java/com/lynchlin/music/network/NeteaseCookieJarTest.kt`
- `app/src/test/java/com/lynchlin/music/network/NeteaseDirectInterceptorTest.kt`
- `app/src/test/java/com/lynchlin/music/network/NeteaseOriginApiServiceStructureTest.kt`

**验收标准**:
- [x] 以上 15 个文件全部不存在
- [x] 删除后 `crypto/` 目录应为空（可一并删除）

---

### TASK-02: 修改 NeteaseSettings.kt — 移除 directMode

**文件**: `app/src/main/java/com/lynchlin/music/data/settings/NeteaseSettings.kt`

**修改内容**:
1. 删除常量 `private const val KEY_DIRECT_MODE = "netease_direct_mode"`（第14行）
2. 删除 `var directMode: Boolean` 属性及其 getter/setter（第42-44行）

**验收标准**:
- [x] 文件中无 `directMode` 相关代码
- [x] 其他属性（apiUrl, cookie, uid, nickname, phone）保持不变

---

### TASK-03: 修改 NeteaseRetrofitClient.kt — 移除直连服务

**文件**: `app/src/main/java/com/lynchlin/music/network/NeteaseRetrofitClient.kt`

**修改内容**:
1. 删除常量 `private const val DIRECT_BASE_URL = "https://music.163.com/"`（第11行）
2. 删除变量 `private var directService: NeteaseOriginApiService? = null`（第15行）
3. 删除变量 `private var directCookieJar: NeteaseCookieJar? = null`（第16行）
4. 删除整个 `getDirectService()` 方法（第44-68行）
5. 删除 `getDirectCookieJar()` 方法（第72行）
6. 删除 `invalidateDirect()` 方法（第75-78行）
7. 保留 `getProxyService()` 和 `invalidate()` 方法不变

**验收标准**:
- [x] 文件中无 `direct` 相关代码
- [x] `getProxyService(baseUrl)` 方法逻辑不变

---

### TASK-04: 修改 NeteaseViewModel.kt — 移除双模式分支逻辑

**文件**: `app/src/main/java/com/lynchlin/music/ui/netease/NeteaseViewModel.kt`

**修改内容**:
1. 删除导入 `import com.lynchlin.music.network.NeteaseOriginApiService`（第9行）
2. 删除方法 `private fun directApi(): NeteaseOriginApiService`（第93-94行）
3. 删除属性 `private val useDirect: Boolean get() = NeteaseSettings.directMode`（第96行）
4. 删除 StateFlow `_directMode`（第59行）和公开属性 `directMode`（第60行）
5. 删除方法 `fun toggleDirectMode()`（第128-136行）
6. 修改 `logout()` 方法：删除 `if (useDirect) { NeteaseRetrofitClient.invalidateDirect() }` 分支（第188-190行）
7. 修改以下所有 API 方法，移除 `if (useDirect)` 分支，仅保留 `else` 分支（即 `api().xxx` 调用）：
   - `login()` — 第149-158行：仅保留 `api().loginCellphone(phone, password)`
   - `loadUserPlaylists()` — 第211-222行：仅保留 `api().userPlaylist(uid, cookie)`
   - `loadPlaylistTracks()` — 第241-254行：仅保留 `api().playlistAllTracks(id, cookie)`
   - `loadDailyRecommend()` — 第277-283行：仅保留 `api().dailyRecommendSongs(cookie)`
   - `loadPersonalized()` — 第305-313行：仅保留 `api().personalizedPrivateContentList(cookie)`
   - `playTrack()` — 第338-349行：仅保留 `api().songUrl(id, cookie)`

**验收标准**:
- [x] 文件中无 `directApi`、`useDirect`、`directMode`、`NeteaseOriginApiService` 相关代码
- [x] 所有 API 调用均通过 `api()` 即代理模式
- [x] 编译无报错

---

### TASK-05: 修改 NeteaseScreen.kt — 移除直连模式 UI

**文件**: `app/src/main/java/com/lynchlin/music/ui/netease/NeteaseScreen.kt`

**修改内容**:
在 `LoginSettingsScreen` Composable 中：
1. 删除变量 `var directMode by remember { mutableStateOf(NeteaseSettings.directMode) }`（第648行）
2. 删除变量 `var showModeChangeDialog by remember { mutableStateOf(false) }`（第649行）
3. 删除整个「连接模式」UI 块（第695-723行）—— 即 `item { HorizontalDivider() ... Switch ... }` 块
4. 删除整个 `AlertDialog` 确认弹窗（第864-891行）—— 即 `if (showModeChangeDialog) { ... }` 块
5. 删除对 `NeteaseSettings` 的导入（如果文件中不再直接引用 `NeteaseSettings`）

**验收标准**:
- [x] LoginSettingsScreen 中无直连/代理切换开关
- [x] 无 `directMode`、`showModeChangeDialog` 变量
- [x] 登录表单 and 登录状态显示保持不变

---

### TASK-06: 修改测试文件

**文件 1**: `app/src/androidTest/java/com/lynchlin/music/data/settings/NeteaseSettingsTest.kt`
- 删除 `NeteaseSettings.directMode = false` 相关代码
- 删除测试用例：`directMode default is false`、`directMode read after write`
- 修改测试用例 `logout does not affect apiUrl and directMode` → 仅验证 `apiUrl` 不受影响

**文件 2**: `app/src/androidTest/java/com/lynchlin/music/network/NeteaseRetrofitClientTest.kt`
- 删除 `NeteaseRetrofitClient.invalidateDirect()` 相关代码
- 删除测试用例：`getDirectService returns non-null`、`getDirectService caches`、`getDirectCookieJar returns non-null`、`invalidateDirect clears`、`NeteaseApiMode enum check`

**验收标准**:
- [x] 测试文件编译通过
- [x] 无 `directMode`、`directService`、`NeteaseApiMode` 相关测试

---

## Part B: 新增心动模式/智能播放

### API 信息

```
端点: GET /playmode/intelligence/list
参数:
  - id    (必填) — 当前歌曲 ID
  - pid   (必填) — 歌单 ID
  - sid   (可选) — 开始播放的歌曲 ID
  - count (可选) — 返回数量
  - cookie (必填) — 登录 Cookie
返回: { code: 200, data: { songs: List<Song> } }
```

---

### TASK-07: 新增心动模式数据模型

**文件**: `app/src/main/java/com/lynchlin/music/data/model/NeteaseModels.kt`

**新增内容**:
```kotlin
// --- Heartbeat / Intelligence Mode ---
data class IntelligenceResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("data") val data: IntelligenceData? = null
)

data class IntelligenceData(
    @SerializedName("songs") val songs: List<NeteaseTrack>? = null
)
```

**验收标准**:
- [x] `IntelligenceResponse` 和 `IntelligenceData` 数据类存在
- [x] 字段与 API 返回结构匹配

---

### TASK-08: 新增心动模式 API 方法

**文件**: `app/src/main/java/com/lynchlin/music/network/NeteaseApiService.kt`

**新增内容**:
```kotlin
// --- Heartbeat / Intelligence Mode ---
@GET("playmode/intelligence/list")
suspend fun intelligenceList(
    @Query("id") id: Long,
    @Query("pid") pid: Long,
    @Query("sid") sid: Long? = null,
    @Query("count") count: Int? = null,
    @Query("cookie") cookie: String
): IntelligenceResponse
```

**验收标准**:
- [x] `intelligenceList` 方法存在且编译通过
- [x] 参数类型和注解正确

---

### TASK-09: 新增心动模式 ViewModel 逻辑

**文件**: `app/src/main/java/com/lynchlin/music/ui/netease/NeteaseViewModel.kt`

**新增内容**:
1. 新增 StateFlow:
   ```kotlin
   private val _intelligenceSongs = MutableStateFlow<List<NeteaseTrack>>(emptyList())
   val intelligenceSongs: StateFlow<List<NeteaseTrack>> = _intelligenceSongs.asStateFlow()
   ```

2. 新增方法:
   ```kotlin
   fun loadIntelligence(songId: Long, playlistId: Long) {
       if (!isLoggedIn) return
       viewModelScope.launch {
           _isLoading.value = true
           _error.value = null
           try {
               val resp = api().intelligenceList(
                   id = songId,
                   pid = playlistId,
                   cookie = NeteaseSettings.cookie
               )
               if (resp.code == 200 && resp.data?.songs != null) {
                   _intelligenceSongs.value = resp.data.songs
               } else {
                   _error.value = "获取心动模式列表失败"
               }
           } catch (e: Exception) {
               _error.value = "获取心动模式列表失败: ${e.message}"
           } finally {
               _isLoading.value = false
           }
       }
   }

   fun playIntelligenceSong(index: Int) {
       val songs = _intelligenceSongs.value
       if (index in songs.indices) {
           playTrack(songs[index], songs, index)
       }
   }

   fun clearIntelligence() {
       _intelligenceSongs.value = emptyList()
   }
   ```

**验收标准**:
- [x] `loadIntelligence` 方法调用 `api().intelligenceList()`
- [x] `playIntelligenceSong` 方法正确播放选中歌曲
- [x] 编译无报错

---

### TASK-10: 新增心动模式 UI

**文件**: `app/src/main/java/com/lynchlin/music/ui/netease/NeteaseScreen.kt`

**修改 1: PlaylistDetailScreen — 添加心动模式按钮**

在「播放全部」按钮旁新增「心动模式」按钮:
```kotlin
FilledTonalButton(onClick = {
    val songId = tracks.firstOrNull()?.id
    val playlistId = playlist?.id
    if (songId != null && playlistId != null) {
        viewModel.loadIntelligence(songId, playlistId)
    }
}) {
    Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp))
    Spacer(Modifier.width(4.dp))
    Text("心动模式")
}
```

**修改 2: 添加心动模式列表展示**

当 `_intelligenceSongs` 不为空时，在 PlaylistDetailScreen 中展示心动模式歌曲列表（复用 `TrackRow` 组件），并提供「返回歌单」按钮调用 `viewModel.clearIntelligence()`。

**验收标准**:
- [x] 歌单详情页有「心动模式」按钮
- [x] 点击后加载智能播放列表
- [x] 列表中的歌曲可点击播放
- [x] 有「返回歌单」按钮清除心动模式列表

---

## Part C: 验证

### TASK-11: 构建验证

**操作**: 执行 `./gradlew assembleDebug`

**验收标准**:
- [x] 编译无错误
- [x] 无 `Unresolved reference` 警告

---

### TASK-12: 测试验证

**操作**: 执行 `./gradlew test`

**验收标准**:
- [x] 所有保留的单元测试通过
- [x] 无因删除直连模式导致的测试失败

---

## 执行顺序

```
TASK-01 (删除文件)
    ↓
TASK-02 → TASK-03 → TASK-04 → TASK-05 → TASK-06 (并行修改)
    ↓
TASK-07 → TASK-08 → TASK-09 → TASK-10 (心动模式新增)
    ↓
TASK-11 → TASK-12 (验证)
```

## 最后更新
2026-05-17 — 重构全部完成。12 个 TASK 全部 ✅。`./gradlew test` BUILD SUCCESSFUL
