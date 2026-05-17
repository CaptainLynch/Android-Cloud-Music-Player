# Learnings - refactor-playlist

## 直连模式 file 清理
- **任务目标**：删除15个直连模式相关文件，并清理空的 `crypto/` 目录。
- **执行结果**：
  - 成功删除了 3 个加密模块文件、4 个直连网络模块文件以及 8 个单元测试文件。
  - 成功识别并清理了 `main/` 和 `test/` 目录下因 file 删除而变空的 `crypto/` 目录。
- **环境与工具问题记录**：
  - **问题**：在 Windows PowerShell (5.1) 环境下，当使用 `git` 相关命令（如 `git rm`）时，平台会自动在命令前拼接 Linux 风格的环境变量设置前缀（`set CI="true" && ...`）。由于 PowerShell 不支持 `set` 和 `&&` 语法，这会导致 `ParserError` 语法错误，使得 `git` 命令无法执行。
  - **解决方案**：在不需要执行复杂 git 历史操作的情况下，可以直接使用 PowerShell 原生的文件操作命令（如 `Remove-Item`）来代替 `git rm`。这样可以避免触发平台的 `git` 注入逻辑，顺利完成文件删除，且文件在 git 状态中仍会被正确识别为已删除（deleted）。

## 移除 NeteaseSettings 中的 directMode
- **任务目标**：修改 `NeteaseSettings.kt`，移除 `directMode` 属性及其常量 `KEY_DIRECT_MODE`。
- **执行结果**：
  - 成功删除了常量 `private const val KEY_DIRECT_MODE = "netease_direct_mode"`。
  - 成功删除了 `var directMode: Boolean` 属性及其 getter/setter。
  - 保持了文件其他部分的完整性和格式整洁。
- **环境与工具问题记录**：
  - 确认了 Kotlin LSP 服务（`kotlin-ls`）在当前环境中未安装，因此无法通过 `lsp_diagnostics` 进行自动语法检查。
  - 验证通过手动读取修改后的文件内容来确保修改的正确性。

## 移除 NeteaseViewModel 中的双模式分支逻辑
- **任务目标**：修改 `NeteaseViewModel.kt`，移除所有直连模式（direct mode）相关的导入、属性、方法 and API 调用分支，仅保留代理模式（proxy mode）。
- **执行结果**：
  - 成功删除了导入 `import com.lynchlin.music.network.NeteaseOriginApiService`。
  - 成功删除了 `_directMode`、`directMode` 属性，`directApi()` 方法，`useDirect` 属性，以及 `toggleDirectMode()` 方法。
  - 成功修改了 `logout()` 方法，移除了 `invalidateDirect()` 分支。
  - 成功修改了 `login()`、`loadUserPlaylists()`、`loadPlaylistTracks()`、`loadDailyRecommend()`、`loadPersonalized()` 和 `playTrack()` 方法，移除了 `if (useDirect)` 分支，仅保留 `api().xxx` 调用。
- **编译与依赖问题记录**：
  - **问题**：在修改完 `NeteaseViewModel.kt` 后，运行 Gradle 编译（`.\gradlew compileDebugKotlin`）会报错，提示 `NeteaseScreen.kt` 中存在对 `directMode` 的未解析引用（Unresolved reference 'directMode'）。
  - **原因**：这是一个多步骤的重构过程，`NeteaseScreen.kt` 的修改属于后续任务（如 TASK-05），在当前阶段尚未进行，因此导致了临时的编译失败。
  - **处理**：根据任务要求，我们不能修改其他文件（`MUST NOT DO: Do NOT modify other files`），因此保持 `NeteaseViewModel.kt` 的修改，等待后续任务对 `NeteaseScreen.kt` 进行相应的清理。

## 移除 NeteaseScreen 中的直连模式 UI
- **任务目标**：修改 `NeteaseScreen.kt`，移除直连模式相关的 UI 元素、变量和弹窗。
- **执行结果**：
  - 成功删除了 `directMode` 和 `showModeChangeDialog` 变量。
  - 成功删除了「连接模式」UI 块（包括 `HorizontalDivider` 和 `Switch` 选项）。
  - 成功删除了 `AlertDialog` 确认弹窗。
  - 成功删除了不再使用的 `import com.lynchlin.music.data.settings.NeteaseSettings` 导入。
- **编译与验证记录**：
  - 在 `CloudMusicPlayer` 目录下运行 `.\gradlew.bat :app:compileDebugKotlin`，编译成功（`BUILD SUCCESSFUL`），无任何编译错误。
  - 解决了之前 TASK-04 修改 `NeteaseViewModel.kt` 后由于 `NeteaseScreen.kt` 尚未修改而导致的临时编译报错，使整个项目恢复到可编译状态。

## 新增心动模式数据模型
- **任务目标**：在 `NeteaseModels.kt` 末尾追加 `IntelligenceResponse` 和 `IntelligenceData` 数据类，用于支持心动模式（Intelligence Mode）。
- **执行结果**：
  - 成功在 `NeteaseModels.kt` 文件末尾追加了 `IntelligenceResponse` 和 `IntelligenceData` 数据类。
  - 保持了文件其他部分的完整性和格式整洁。
- **编译与验证记录**：
  - 在 `CloudMusicPlayer` 目录下运行 `.\gradlew :app:compileDebugKotlin`，编译成功（`BUILD SUCCESSFUL`），无任何编译错误。
  - 验证了新增的数据模型与已有的 `NeteaseTrack` 结构完全兼容。

## 移除测试文件中的直连模式引用 (TASK-06)
- **任务目标**：修改 `NeteaseSettingsTest.kt` 和 `NeteaseRetrofitClientTest.kt`，移除所有直连模式相关测试用例。
- **执行结果**：
  - `NeteaseSettingsTest.kt`：将 `logout does not affect apiUrl and directMode` 改为仅验证 `apiUrl`。
  - `NeteaseRetrofitClientTest.kt`：删除了 `getDirectService caches service`、`getDirectCookieJar`、`invalidateDirect clears`、`NeteaseApiMode enum check` 共 4 个测试；从 `tearDown` 中移除 `invalidateDirect()` 调用。
- **保留的测试**：5 个 proxy service 相关测试全部保留。

## 新增心动模式 ViewModel 逻辑 (TASK-09)
- **任务目标**：在 `NeteaseViewModel.kt` 中添加心动模式的 StateFlow 和方法。
- **执行结果**：
  - 新增 `_intelligenceSongs` StateFlow 和 `intelligenceSongs` 公开属性。
  - 新增 `loadIntelligence(songId, playlistId)` — 调用 `api().intelligenceList()` 获取智能播放列表。
  - 新增 `playIntelligenceSong(index)` — 播放选中的心动模式歌曲。
  - 新增 `clearIntelligence()` — 清空心动模式列表。

## 新增心动模式 UI (TASK-10)
- **任务目标**：在 `NeteaseScreen.kt` 的 `PlaylistDetailScreen` 中添加心动模式按钮 and 智能播放列表展示。
- **执行结果**：
  - 在「播放全部」按钮旁新增「心动模式」FilledTonalButton。
  - 点击后调用 `viewModel.loadIntelligence(songId, playlistId)`。
  - 当 `intelligenceSongs` 不为空时，切换显示心动模式歌曲列表（含「返回歌单」按钮）。
  - 复用 `TrackRow` 组件展示歌曲列表。

## 构建验证 (TASK-11/12)
- **`.\gradlew :app:compileDebugKotlin`**：BUILD SUCCESSFUL ✅
- **注意**：`.\gradlew :app:test` 原有 3 个预存编译错误 + 1 个断言失败，本轮全部修复：
  - `ConvertersTest.kt` — `TypeConverters` → `MusicTypeConverters`（7处）
  - `FavoriteEntityTest.kt` — 补充缺失的 `artistJson`/`album`/`picId`/`urlId`/`lyricId`/`source` 实参（传 null）
  - `PlayerServiceStructureTest.kt` — 删除 `MusicServiceStructureTest` 类（MusicService 已被 F10 移除）
  - `MediaPlaybackService.kt` — `getInstance()` 加 `@JvmStatic` 注解（Java 反射要求）
- **最终 `.\gradlew :app:test`**: BUILD SUCCESSFUL, 35 tests passed ✅

## 修复 FavoriteEntityTest 编译错误 (TASK-13)
- **任务目标**：修复 `FavoriteEntityTest.kt` 编译错误。`FavoriteEntity` 构造函数要求所有参数（包括可空参数）必须显式传递，测试代码多处省略了 `artistJson`、`album`、`picId`、`urlId`、`lyricId`、`source` 等参数。
- **执行结果**：
  - 成功在 `FavoriteEntityTest.kt` 中为所有 `FavoriteEntity` 构造函数调用补全了缺失的参数，并将它们显式传递为 `null`。
  - 保持了测试逻辑和 `addedAt` 默认值测试的正确性。
- **编译与验证记录**：
  - 在 `CloudMusicPlayer` 目录下运行 `.\gradlew.bat :app:testDebugUnitTest --tests "com.lynchlin.music.data.local.FavoriteEntityTest"`，编译成功且测试全部通过（`BUILD SUCCESSFUL`）。
