# GitHub push troubleshooting

## `repository not found` for Ticket-Management-System

If you see:

```text
fatal: repository 'https://github.com/ckrsingh/Ticket-Management-System.git/' not found
```

the repo on GitHub is **`ckrsingh/-Ticket-Management-System`** (with a leading `-`), not `Ticket-Management-System`. Verified: the hyphenated name exists; the name without hyphen does not.

**Fix — push to the existing repo:**

```bash
cd ~/cursor-projects/Ticket-Management-System
git remote set-url origin https://github.com/ckrsingh/-Ticket-Management-System.git
git push -u origin main
```

**Or — rename on GitHub** (if you want a clean name without hyphen):

1. Open https://github.com/ckrsingh/-Ticket-Management-System → **Settings** → **General** → Repository name → `Ticket-Management-System` → Rename.
2. Then:

```bash
git remote set-url origin https://github.com/ckrsingh/Ticket-Management-System.git
git push -u origin main
```

## 1. Use the renamed folder

After `mv ... ai-support-tickets ... Ticket-Management-System`, **do not** `cd` into `ai-support-tickets` (that path is gone).

```bash
cd ~/cursor-projects/Ticket-Management-System
git remote -v
# origin  https://github.com/ckrsingh/-Ticket-Management-System.git
```

## 2. Create the GitHub repo (if you have not)

On https://github.com/new :

- Name: `Ticket-Management-System`
- **Do not** add README, `.gitignore`, or license (you already have local commits)

Or with GitHub CLI (after `gh auth login`):

```bash
gh repo create Ticket-Management-System --private --source=. --remote=origin --push
```

## 3. Authenticate (most common blocker)

HTTPS `git push` needs credentials. In a normal terminal (not a headless agent), use **one** of these:

### Option A — GitHub CLI (recommended)

```bash
sudo snap install gh   # or sudo apt install gh
gh auth login
gh auth setup-git
cd ~/cursor-projects/Ticket-Management-System
git push -u origin main
```

### Option B — SSH

```bash
ssh-keygen -t ed25519 -C "your_email@example.com"
cat ~/.ssh/id_ed25519.pub   # add at GitHub → Settings → SSH keys

git remote set-url origin git@github.com:ckrsingh/Ticket-Management-System.git
git push -u origin main
```

### Option C — Personal access token (HTTPS)

1. GitHub → **Settings → Developer settings → Personal access tokens** (classic: `repo` scope).
2. When `git push` prompts for password, paste the **token** (not your GitHub password).

Or use the credential helper so you are not prompted every time:

```bash
git config --global credential.helper store   # optional; stores in ~/.git-credentials
git push -u origin main
```

Never commit tokens into the repository.

## 4. Typical error messages

| Message | What to do |
|---------|------------|
| `could not read Username for 'https://github.com'` | No credential helper / non-interactive shell — run push in your own terminal after `gh auth login` or SSH. |
| `Repository not found` | Repo does not exist, wrong URL, or no access — create repo or fix `git remote set-url`. |
| `Authentication failed` | Wrong or expired PAT; re-login with `gh auth login` or update SSH key. |
| `Updates were rejected` (non-fast-forward) | Remote has commits (e.g. README on create) — `git pull origin main --rebase` then push, or overwrite only if intentional: `git push -u origin main --force-with-lease`. |
| `Permission denied (publickey)` | SSH key not added to GitHub or wrong remote URL. |

## 5. Verify before push

```bash
git status          # clean working tree
git log -2 --oneline
git remote -v
```

Expected: branch `main`, commits on `main`, `origin` → `https://github.com/ckrsingh/-Ticket-Management-System.git` (unless you renamed the repo on GitHub).

## 6. Keep work email out of commit history

Git stores **author email in every commit**, not only in files. Use GitHub’s private noreply address (Settings → Emails → “Keep my email addresses private”):

```bash
git config user.email "ckrsingh@users.noreply.github.com"
git config user.name "Chandan"
```

If commits were already created with a work email **before the first push**, rewrite local history:

```bash
FILTER_BRANCH_SQUELCH_WARNING=1 git filter-branch -f --env-filter '
export GIT_AUTHOR_NAME="Chandan"
export GIT_AUTHOR_EMAIL="ckrsingh@users.noreply.github.com"
export GIT_COMMITTER_NAME="$GIT_AUTHOR_NAME"
export GIT_COMMITTER_EMAIL="$GIT_AUTHOR_EMAIL"
' -- main
```

If you already pushed, fixing history requires `git push --force-with-lease` (coordinate with anyone else using the repo).
