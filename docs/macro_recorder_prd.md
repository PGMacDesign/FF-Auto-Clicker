# MacroX Product Requirements Document (PRD)

## Title & Summary
**Project Name:** MacroX  
**Purpose / Vision:**  
To build a cross-platform (Windows & Linux) macro / input replay tool that allows recording, editing, and deterministic + randomized playback of mouse + keyboard actions, with flexibility over timing, variability, and branching.

**Scope (v1 MVP):**  
- Basic recording / playback  
- Editing timing / coordinates  
- Randomization (delay jitter, click offset)  
- Branching / “special event” insertion  
- Basic UI / macro management  

**Out of Scope (initially):**  
- Integration with games  
- Process injection or anti-cheat bypass  
- Mobile version or non-desktop OSes  
- Advanced UI animations  

---

## Stakeholders & Users
- **Primary user:** Power users who want to record and replay custom macros  
- **Secondary users:** Technical users seeking automation tools  
- **Stakeholders:** Developer / QA team  

---

## Success Metrics
- 90% playback accuracy on simple macros  
- Cross-platform compatibility  
- Editing macros under 10 minutes  
- Stable playback for 10,000+ iterations  

---

## Use Cases
| ID | Persona | Want to Do | So That |
|----|----------|-------------|----------|
| UC1 | Power User | Press record, perform actions, stop with F12 | Capture a macro |
| UC2 | Power User | Name & save macro | Reuse it later |
| UC3 | Power User | Edit delay/coords | Fine-tune playback |
| UC4 | Power User | Add randomization | Mimic human variation |
| UC5 | Power User | Insert special events | Branch behaviors |
| UC6 | Power User | Loop macro | Automate tasks |
| UC7 | Power User | Abort playback | Stop quickly |
| UC8 | Power User | Import/export macros | Share or backup |
| UC9 | Power User | Cross-platform usage | Use macros anywhere |

---

## Core Features

### 1. Recording
- Countdown before record starts  
- Capture mouse & keyboard events (move, click, press/release)  
- Stop recording with F12  
- Save as timestamped JSON-like event log  

### 2. Macro Management
- Name, rename, list macros  
- Store in cross-platform format (JSON/YAML)  
- Import/export macros  

### 3. Playback
- Play via hotkey or button  
- Run once, N times, or infinitely  
- Randomization applied dynamically  
- Interrupt playback anytime  

### 4. Editing
- View event list with timestamps & coords  
- Modify times, x/y positions  
- Insert/remove/reorder events  
- Optional coordinate grid UI  

### 5. Randomization
- Delay jitter (user-defined min/max range)  
- Click offset randomization (1–100 px range)  
- User-defined frequency/probability  
- Uniform or Gaussian distribution (optional)  

### 6. Branching / Special Events
- Insert submacro every N runs  
- Weighted probability for alternate sequences  
- Pause parent macro while running submacro  

### 7. Platform Support
- Windows: `SendInput` API  
- Linux: X11 / XTest / uinput layer  
- Cross-platform toolkit (Qt, Electron, GTK)  

### 8. Safety & Logging
- Hotkey abort (configurable)  
- Revert partial actions on abort  
- Playback logs with applied randomness  

### 9. UI / UX
- Simple main UI with macro list & config panel  
- Randomization sliders, branching checkboxes  
- Status indicators (Recording, Playing, Idle)  
- Error handling for invalid macros or bounds  

---

## Technical Architecture
**Core Modules:**  
- Recording Engine  
- Playback Controller  
- Event Storage & Parser  
- Editor Interface  
- Cross-Platform I/O Layer  

**Data Flow:**  
1. User records macro → raw event stream  
2. Stream saved as macro file  
3. Playback controller replays stream with randomness applied  
4. Logger records execution trace  

---

## Data Model (YAML-like)
```yaml
Macro:
  id: string
  name: string
  events: [Event]
  randomization:
    delay_range: [min, max]
    delay_freq: { every_nth: int, probability: float }
    click_offset_range: [min, max]
    click_offset_probability: float
  branching:
    enabled: bool
    every_n_runs: int
    branch_macro: string
    branch_probability: float

Event:
  timestamp: float
  type: [MouseMove, MouseDown, MouseUp, KeyDown, KeyUp]
  x: int
  y: int
  key: string
```

---

## Milestones / Roadmap
1. **v0.1 MVP:** Basic record/play, UI skeleton  
2. **v0.2:** Editing + randomization  
3. **v0.3:** Branching macros  
4. **v1.0:** Cross-platform parity, logging, error handling  

---

## Risks
- Timing precision  
- OS-level permission issues  
- Input API differences (Windows vs Wayland)  
- User error in coordinate mapping  

---

## Acceptance Criteria
- Macro playback within ±5 ms of target  
- Delay jitter and click offset match configuration  
- Branch macro triggers correctly every N runs  
- Abort key immediately halts playback  
- Same macro file works cross-platform  

---

## Open Questions
- Which randomization distribution to default to?  
- Should branch macros reset iteration counter?  
- Best format for editing timeline (spreadsheet vs UI)?  
- Minimum jitter precision (ms vs µs)?  

