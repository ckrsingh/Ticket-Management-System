# GitHub push troubleshooting

## 1. Use the renamed folder

After `mv ... ai-support-tickets ... Ticket-Management-System`, **do not** `cd` into `ai-support-tickets` (that path is gone).

```bash
cd /home/chandan/cursor-projects/Ticket-Management-System
git remote -v
# origin  https://github.com/ckrsingh/Ticket-Management-System.git
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
cd /home/chandan/cursor-projects/Ticket-Management-System
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

Expected: branch `main`, 2 commits, `origin` → `https://github.com/ckrsingh/Ticket-Management-System.git`.
