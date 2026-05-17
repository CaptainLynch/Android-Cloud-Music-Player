# Bug 记录 — 应用闪退

**日期**: 2026-05-14 23:24
**严重程度**: 🔴 Critical — 应用启动即崩溃

---

## 崩溃堆栈

```
Caused by: java.lang.IllegalStateException: FavoritesRepository not initialized. Call init(context) first.
    at com.lynchlin.music.data.repository.FavoritesRepository.getDao(FavoritesRepository.kt:19)
    at com.lynchlin.music.data.repository.FavoritesRepository.init(FavoritesRepository.kt:30)
    at com.lynchlin.music.ui.SearchViewModel.<init>(SearchViewModel.kt:52)
    at java.lang.reflect.Constructor.newInstance0(Native Method)
    at java.lang.reflect.Constructor.newInstance(Constructor.java:343)
    at androidx.lifecycle.ViewModelProvider$AndroidViewModelFactory.create(ViewModelProvider.android.kt:299)
    ... 101 more
```

---

## 根因分析

**文件**: `data/repository/FavoritesRepository.kt` 第 19 行

```kotlin
private var dao: FavoriteDao? = null
    get() = field ?: error("FavoritesRepository not initialized. Call init(context) first.")
```

自定义 getter 在 `dao` 为 null 时直接抛异常。

**调用链**:
1. `SearchViewModel.<init>` → 调用 `FavoritesRepository.init(application)` (第 52 行)
2. `FavoritesRepository.init()` → 执行 `if (dao != null) return` 检查是否已初始化 (第 30 行)
3. 访问 `dao` 触发自定义 getter → `field` 为 null → 抛出 IllegalStateException

**循环依赖**: `init()` 想检查 `dao` 是否为 null 来决定是否需要初始化，但访问 `dao` 本身就会因为未初始化而抛异常。init 方法永远无法执行到实际的初始化代码。

---

## 调用关系

```
SearchViewModel.init (line 52)
  → FavoritesRepository.init(context)
    → if (dao != null) return   ← 访问 dao 触发 getter → throw!
    → ... (永远不会执行到这里)
```

---

## 修复方案

**方案**: 移除自定义 getter，改用普通可空属性。`toggleFavorite()` 等调用点由 `init()` 确保已初始化，无需 getter 兜底。

```kotlin
// 修改前
private var dao: FavoriteDao? = null
    get() = field ?: error("FavoritesRepository not initialized. Call init(context) first.")

// 修改后
private var dao: FavoriteDao? = null
```

---

## 影响范围

- SearchViewModel 创建时触发 — 所有使用该 ViewModel 的页面（SearchScreen、PlayerScreen、FavoritesScreen）均无法打开
- 应用实际无法启动到主界面
