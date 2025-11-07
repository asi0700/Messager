# Подключение к GitHub через консоль

## Шаг 1: Настройка Git (если еще не сделано)

```bash
git config --global user.name "Ваше Имя"
git config --global user.email "firemagers097@gmail.com"
```

## Шаг 2: Проверка подключения к GitHub

Проверьте, что Git установлен:
```bash
git --version
```

## Шаг 3: Способы аутентификации в GitHub

Есть два основных способа подключения к GitHub:

### Способ 1: HTTPS с Personal Access Token (рекомендуется)

#### 3.1. Создание Personal Access Token на GitHub:

1. Зайдите на GitHub.com и войдите в свой аккаунт
2. Перейдите в **Settings** (Настройки) → **Developer settings** → **Personal access tokens** → **Tokens (classic)**
3. Нажмите **"Generate new token"** → **"Generate new token (classic)"**
4. Дайте токену имя (например, "My Computer")
5. Выберите срок действия (expiration)
6. Выберите права доступа (scopes):
   - ✅ **repo** (полный доступ к репозиториям)
   - ✅ **workflow** (если используете GitHub Actions)
7. Нажмите **"Generate token"**
8. **ВАЖНО:** Скопируйте токен сразу! Он больше не будет показан!

#### 3.2. Использование токена:

При первом `push` или `pull` Git запросит:
- **Username:** ваш GitHub username
- **Password:** вставьте Personal Access Token (не ваш пароль!)

Или можно сохранить токен в Git Credential Manager:
```bash
git config --global credential.helper manager-core
```

### Способ 2: SSH ключи (более безопасно)

#### 3.1. Генерация SSH ключа:

```bash
ssh-keygen -t ed25519 -C "firemagers097@gmail.com"
```

Нажмите Enter для всех вопросов (или укажите путь к файлу).

#### 3.2. Добавление SSH ключа в GitHub:

1. Скопируйте публичный ключ:
   ```bash
   type %USERPROFILE%\.ssh\id_ed25519.pub
   ```
   (или `cat ~/.ssh/id_ed25519.pub` в Git Bash)

2. На GitHub:
   - Перейдите в **Settings** → **SSH and GPG keys**
   - Нажмите **"New SSH key"**
   - Вставьте скопированный ключ
   - Нажмите **"Add SSH key"**

#### 3.3. Проверка SSH подключения:

```bash
ssh -T git@github.com
```

Должно появиться: "Hi username! You've successfully authenticated..."

## Шаг 4: Подключение к существующему репозиторию

### Если репозиторий уже существует на GitHub:

1. **Перейдите в папку проекта:**
   ```bash
   cd C:\Users\MataDoRs\Desktop\Messager-master
   ```

2. **Инициализируйте Git (если еще не сделано):**
   ```bash
   git init
   ```

3. **Добавьте удаленный репозиторий:**

   **Для HTTPS:**
   ```bash
   git remote add origin https://github.com/ВАШ_USERNAME/НАЗВАНИЕ_РЕПОЗИТОРИЯ.git
   ```

   **Для SSH:**
   ```bash
   git remote add origin git@github.com:ВАШ_USERNAME/НАЗВАНИЕ_РЕПОЗИТОРИЯ.git
   ```

   Замените:
   - `ВАШ_USERNAME` - ваш GitHub username
   - `НАЗВАНИЕ_РЕПОЗИТОРИЯ` - название вашего репозитория

4. **Проверьте подключение:**
   ```bash
   git remote -v
   ```

   Должно показать:
   ```
   origin  https://github.com/username/repo.git (fetch)
   origin  https://github.com/username/repo.git (push)
   ```

5. **Получите код из GitHub:**
   ```bash
   git pull origin main
   ```
   или
   ```bash
   git pull origin master
   ```
   (зависит от названия основной ветки в вашем репозитории)

## Шаг 5: Первая отправка кода на GitHub

Если это новый репозиторий или первый раз отправляете код:

```bash
# Добавить все файлы
git add .

# Создать коммит
git commit -m "Initial commit"

# Отправить на GitHub
git push -u origin main
```

Если основная ветка называется `master`:
```bash
git push -u origin master
```

## Шаг 6: Работа с репозиторием

### Получить последние изменения:
```bash
git pull origin main
```

### Отправить свои изменения:
```bash
git add .
git commit -m "Описание изменений"
git push origin main
```

## Полезные команды для работы с GitHub

### Просмотр информации о репозитории:
```bash
git remote show origin
```

### Изменение URL репозитория:
```bash
git remote set-url origin https://github.com/username/new-repo.git
```

### Удаление подключения к репозиторию:
```bash
git remote remove origin
```

### Просмотр всех веток (включая удаленные):
```bash
git branch -a
```

### Создание и отправка новой ветки:
```bash
git checkout -b новая-ветка
# ... внесите изменения ...
git add .
git commit -m "Изменения в новой ветке"
git push -u origin новая-ветка
```

## Решение проблем

### Проблема: "fatal: remote origin already exists"
```bash
# Удалите старое подключение
git remote remove origin
# Добавьте заново
git remote add origin https://github.com/username/repo.git
```

### Проблема: "Permission denied"
- Проверьте правильность токена/SSH ключа
- Убедитесь, что у вас есть права доступа к репозиторию

### Проблема: "Authentication failed"
- Для HTTPS: используйте Personal Access Token, а не пароль
- Для SSH: проверьте, что ключ добавлен в GitHub

### Проблема: "Repository not found"
- Проверьте правильность URL репозитория
- Убедитесь, что репозиторий существует и у вас есть к нему доступ

## Быстрая справка

**Найти URL вашего репозитория на GitHub:**
1. Зайдите на страницу репозитория на GitHub
2. Нажмите зеленую кнопку **"Code"**
3. Скопируйте URL (HTTPS или SSH)

**Проверить текущее подключение:**
```bash
git remote -v
```

**Проверить статус:**
```bash
git status
```


