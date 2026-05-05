# 音乐播放器项目 AI 开发上下文 (AI Context)

## 1. 项目概述
这是一个 Android 原生音乐播放器应用。
- **技术栈**: Kotlin, Jetpack Compose (UI), Androidx Media3 / ExoPlayer (播放内核), Retrofit + OkHttp (网络请求), Coroutines & Flow (异步处理)。
- **架构模式**: MVVM (Model-View-ViewModel) + Clean Architecture。

## 2. 核心功能与 API 规范
### 2.1 基础音乐功能 (基于 GD API)
- **API 基础地址**: `https://music-api.gdstudio.xyz/api.php`
- **要求实现**: 搜索音乐、获取歌曲播放链接、获取专辑封面、获取滚动歌词。
- *AI 注意事项*: 所有的网络请求必须在 ViewModel 的协程中处理，使用 Retrofit 封装接口。

### 2.2 网易云音乐高级功能
- **需求**: 根据网易云用户 ID 读取歌单（包含“我喜欢的音乐”）、读取每日推荐、私人雷达、私人漫游。
- **技术难点**: 涉及用户 Token/Cookie 验证。
- *解决方案预设*: 需要集成并使用开源的网易云 API（如 Binaryify 的 NeteaseCloudMusicApi 的自建服务，或寻找可用的公用接口来传递 cookie/token）。

## 3. 开发规范 (AI Agent Rules)
1. **优先使用现代 Android 技术**: 坚决避免使用 Java、XML 布局或过时的 API，全部使用 Kotlin 和 Compose。
2. **代码接力原则**: 每次修改代码后，请更新下方的【当前进度与待办事项】，以便下一个 AI 知道接下来的工作。
3. **模块化**: 网络请求层、UI 播放层、数据层必须解耦。

## 4. 当前进度与待办事项 (Task Tracker)
- [x] 初始化项目结构与 `GEMINI.md` 文档。
- [x] 任务 1: 使用 Android Studio 搭建基础的 Compose 空白项目。
- [x] 任务 2: 编写 Retrofit 网络层，对接 GD Studio API。
- [x] 任务 3: 实现主界面的搜索 UI 与结果列表展示。
- [ ] 任务 4: 集成 Media3/ExoPlayer，实现基础的音频播放与控制。
- [ ] 任务 5: 研究并攻克网易云个人歌单（需要鉴权）的 API 获取机制。