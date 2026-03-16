# Game Tree Project (MI Course)

This project is developed as part of the **Introduction to Artificial Intelligence** course.  
The goal of the project is to implement a **deterministic two-player game with full information**, where a human plays against the computer.

The program will use **game tree search algorithms** such as **Minimax** and **Alpha–Beta pruning**.

---

## Technologies

The project is implemented using:

- **Java**
- **Maven** (project and dependency management)

---

## Game Description

At the beginning of the game:

- The player chooses a starting number **from 8 to 18**.
- Both players start with **0 points**.

Players take turns multiplying the current number by:

- **2**
- **3**
- **4**

Rules:

- If the result is **even**, the opponent **loses 1 point**.
- If the result is **odd**, the current player **gains 1 point**.

The game ends when the number becomes **greater than or equal to 1200**.

The player with the **higher score wins**.
