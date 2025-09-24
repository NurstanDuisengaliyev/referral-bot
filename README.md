# 🤖 Referral Bot

A **Telegram bot** that connects job seekers (Applicants) with company insiders (Referrers) to simplify and automate the referral process.  
Built with **Spring Boot, PostgreSQL, Docker, Flyway**, and deployed on **DigitalOcean**.

[Link](https://t.me/IT_Referral_Bot) to Telegram Bot.

---

## ✨ Features

### Applicant Flow
- Register with name, email, CV (uploaded as Telegram document), skills, and desired companies.
- Submit referral requests directly in Telegram.
- Track referral status (pending / approved / rejected).
- Re-apply when referrals expire.

### Referrer Flow
- Register with name and company.
- Review applicant profiles (name, email, skills, CV) directly in-bot.
- Approve or reject referral requests with one click.

### Automation
- Daily scheduler sends:
  - Digest to referrers (`X applicants are waiting for review`).
  - Digest to applicants with approved referrals (`Contact @username for details`).
- Referrals automatically expire after 7 days.
- Cleanup job removes outdated referrals.

---

## 🛠️ Tech Stack

- **Backend**: Kotlin / Java, Spring Boot  
- **Database**: PostgreSQL + Flyway migrations  
- **Deployment**: Docker, Docker Compose, DigitalOcean  
- **Messaging**: Telegram Bot API  
- **Persistence**: JPA/Hibernate with custom queries  

---

## 🚀 Getting Started

### 1. Clone the repo
```bash
git clone https:/NurstanDuisengaliyev/github.com//referral-bot.git
cd referral-bot/referral-bot
```
### 2. Create a new bot with @BotFather in Telegram and copy the bot token.
### 3. Configure environment variables.
```bash
DB_URL=jdbc:postgresql://db:5432/referral_db
DB_NAME=referral_db
DB_USER=referral_user
DB_PASSWORD=securepass
TELEGRAM_BOT_TOKEN=your-telegram-bot-token
```
### 4. Build and run.
```bash
mvn clean package -DskipTests
docker compose up -d --build
```
### 5. Open Telegram, find your bot, and start chatting as Applicant or Referrer.
