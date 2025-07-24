# ♟️ Java Checker Game

Welcome to my **Checker Game**, a desktop application built with **Java Swing**!  
This was a major milestone project for me, completed in **9 days** from scratch. I created it to strengthen my understanding of Java GUI, object-oriented design, and AI decision-making in games.

---

## 🎮 Features

✅ **Complete Game Logic**
- Movement (diagonal moves)
- Capturing opponent pieces (single and multiple jumps)
- King promotion (with backward movement)
- Turn switching

🧠 **AI Opponent**
- Evaluates possible moves and captures
- Handles **chain captures**
- Selects moves using a basic score-based system
- Priotize King promoting movement and capturable movement
- Know how to move futher if it is about to be capture

🖥️ **Graphical Interface**
- Built using **Java Swing**
- Highlighted moves and turn indicators
- Interactive checker pieces with mouse controls

🧹 **Clean Code Architecture**
- Modular design with separation between GUI, logic, and utilities
- Easily extendable for future features

---

## 🚧 Planned Improvements

- 🎨 Better board and checker designs
- 🔊 Sound effects for moves and captures
- 🎞️ Smooth animations
- 🧠 More strategic and challenging AI

---

## 🗂️ Project Structure

- `Main.java` – Entry point of the application
- `WelcomeWindow.java` – Start screen with options
- `SimpleChessBoard.java` – Main game board UI
- `CheckerGameLogic.java` – Core game logic including AI
- Additional helper classes for pieces, positions, and utilities

---



## 🏁 Getting Started

### Prerequisites
- JDK 8 or later
- Java-compatible IDE (e.g., IntelliJ, Eclipse, VS Code)

### To Run Locally
1. Clone the repo:
   ```bash
   git clone https://github.com/your-username/java-checker-game.git
   cd java-checker-game


📦 Packaging & Distribution
I attempted to create a standalone .exe or .jar installer for friends to play easily, but it’s still a work-in-progress. If you have experience in creating installable desktop apps from Java Swing projects, feel free to open a discussion or PR — I’m eager to learn!

🤝 Contributions & Feedback
Got ideas to improve the game? Found a bug?
Feel free to open an issue or submit a pull request.

📬 Contact
Feel free to connect with me on linkedin.com/in/waing-wai-wai-phyo-a974a8273
