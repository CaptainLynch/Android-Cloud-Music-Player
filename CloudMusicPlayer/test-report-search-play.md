# 测试报告 — 搜索/获取/播放链路

**日期**: 2026-05-24
**测试范围**: GD Studio API 搜索、URL 获取、播放链路

---

## API 端点测试

### Base URL: `https://api.i-meto.com/meting/api`

| 测试项 | 端点 | 结果 | 详情 |
|--------|------|------|------|
| 网易云搜索 | `server=netease&type=search&id=test` | ✅ 200 | 返回 30+ 条结果，数据完整 |
| 网易云搜索(中文) | `server=netease&type=search&id=晴天` | ✅ 200 | 返回正确中文歌曲 |
| 网易云 URL | `server=netease&type=url&id=2652820720&auth=...` | ✅ 302→200 | 重定向到 m802.music.126.net CDN，481KB mp3 |
| 酷狗搜索 | `server=kugou&type=search&id=晴天` | ✅ 200 | 返回 30+ 条结果 |
| 酷我搜索 | `server=kuwo&type=search&id=test` | ⚠️ 200 | 返回数据但 `id=undefined`，URL 不可用 |
| 咪咕搜索 | `server=migu&type=search&id=晴天` | ❌ 400 | "server 参数不合法" — 咪咕源已从 API 移除 |

---

## 代码链路审查

### 搜索流程 ✅
```
SearchViewModel.searchMusic(keyword)
  → RetrofitClient.apiService.searchMusic(server, keyword)
  → List<MetingSong> (title, author, url, pic, lrc)
  → 映射为 Song(id=hash(url), urlId=url, picId=pic)
```
无问题。

### 播放流程 ✅
```
SearchViewModel.playSong(song)
  → MusicPlayerManager.playQueue(results, startIndex)
  → playSongFromQueue(song)
  → MusicPlayerManager.playExternalUrl(urlId, song)
  → ExoPlayer.setMediaItem(MediaItem.fromUri(url))
  → ExoPlayer.prepare() + play()
```
ExoPlayer 内置 HTTP 客户端，自动跟随 302 重定向。无问题。

### 封面加载流程 ✅
```
SongItem → LaunchedEffect(picId)
  → viewModel.loadAlbumArt(picId) → 直接返回 picId（已是完整 URL）
  → Coil AsyncImage(model=url)
```
无问题。

---

## 发现的问题

| ID | 严重程度 | 问题 | 影响 |
|----|---------|------|------|
| ISS-001 | 中 | 咪咕音源 API 返回 400 | 选择咪咕平台搜索会返回错误，用户体验差 |
| ISS-002 | 低 | 酷我音源返回 `id=undefined` | 搜索结果中酷我歌曲 URL 不可用 |

---

## 结论

搜索、获取、播放的 **核心链路无代码缺陷**。网易云和酷狗音源正常工作。
咪咕源需要从 `availablePlatforms` 列表中移除或标记为不可用。
