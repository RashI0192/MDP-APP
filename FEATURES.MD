# Code fixing 
- Removed the crashing compass icon and click handler.
- Removed the manual obstacle switch/slider.
- Added manual X/Y selection from 0–19.
- Added North, South, East, and West direction selection.
- Added a live list of current obstacles.
- Added obstacle update, cancel, and delete options.
- Fixed obstacle numbering after deletions and new additions.
- Fixed manual obstacle updates on the grid.
- Added Task 1 Pause/Resume controls.
- Added Task 2 Pause/Resume controls.
- Added Practice Tasks mode.
- Added Start, Pause/Resume, Stop, and Lapse features.
- Added editable descriptions for practice lapses.
- Changed the grid background to white.
- Changed explored cells to soft pale gold.
- Changed blue buttons to a gold/yellow theme.
- Applied lag fixes so the app runs smoother - bluetooth, cache, sleep timer, grid load.
- Debugged the Cancel button.
- Debugged the Disconnect button.
- Preserved all existing Bluetooth and RPi communication logic.
- Removed Load icon/action — simplified map controls.
- Removed hidden emergency control — eliminated unused hidden functionality.
- Added Reset Map confirmation — prevents accidental obstacle deletion.
- Reset now clears the entire grid — gives a complete map reset.
- Added Delete Obstacle mode — allows quick tap-to-delete editing.
- Fixed obstacle direction-marker alignment — keeps red markers inside the correct cell.
- Fixed drag-and-drop numbering — dragged objects now display their obstacle number.
- Removed Toast notifications — improves speed during repeated add, drag, and delete actions.
- Kept Bluetooth behavior unchanged — no unnecessary communication changes were made.


## Main screen controls (see how improve)

### Robot movement pad

The directional icons send movement commands through Bluetooth:

- Up arrow → sends `f` for forward
- Down arrow → sends `b` for backward
- Left arrow → sends `fl` for forward-left/turn-left
- Right arrow → sends `fr` for forward-right/turn-right
- Bottom-left arrow → sends `bl` for backward-left
- Bottom-right arrow → sends `br` for backward-right

Each button first updates the robot position locally on the grid, then sends the command to the RPi.

The robot only moves if a valid start point has been configured.

### Bluetooth icon

The Pokéball/Bluetooth icon opens the Bluetooth setup screen.

There you can:

- Enable or disable Bluetooth
- Scan for devices
- Select paired or discovered devices
- Connect to the RPi
- Return to the main screen

The actual transmission is centralized through `Home.printMessage()`, which writes bytes to the existing Bluetooth connection. None of the UI changes we made alter this path.

## Map configuration screen

### Set Start Point

This toggle enables start-point selection.

After enabling it, tapping a grid cell:

- Sets the robot’s starting position
- Updates the X/Y labels
- Sets the robot direction
- Draws the robot on the grid

The robot occupies a 2×2 area, so valid robot placement is more restricted than obstacle placement.

### Add Obstacle icon

The tree icon enables obstacle placement mode.

When active, tapping a grid cell:

- Checks whether that cell is already occupied
- Stores the obstacle coordinate
- Stores its direction/image-bearing metadata
- Draws the obstacle on the grid
- Sends an `OBSTACLE` message through the existing Bluetooth layer

The icon itself does not directly communicate with Bluetooth; the grid model calls the existing communication method after updating its data.

### Save icon

Saves the current obstacle list locally into shared preferences.

The saved data contains:

- X coordinate
- Y coordinate
- Direction

It does not change the RPi connection.

### Load icon

Loads the locally saved obstacles and redraws them on the grid.

The loaded obstacles are inserted back into the local grid model. They can then be sent using the existing Send Obstacles action.

### Reset icon

Clears the local map and sends:

```text
CLEAR
```

This is an RPi-facing action, so it should remain clearly labeled and ideally require confirmation in the future.

### Drag switch

Enables dragging obstacles around the grid.

When an obstacle is dragged:

- Its old cell is cleared
- Its new coordinate is stored
- The grid redraws it
- An updated `OBSTACLE` message is sent

### Change Obstacle switch

Enables changing obstacle image/direction information by tapping an obstacle.

This is separate from dragging:

- Dragging changes position
- Change Obstacle changes obstacle metadata

### Hidden emergency icon

The small hidden image area cycles through emergency/path options after repeated taps. It is currently not obvious to a user and appears to be a testing or legacy feature.

I would recommend either removing it from the visible UI or replacing it with a clearly labeled emergency control.

## Manual obstacle input

The manual obstacle screen is a second way to define obstacles.

It allows:

- X coordinate: 0–19
- Y coordinate: 0–19
- Direction: North, South, East, West
- Add obstacle
- View current obstacles
- Select an obstacle
- Update its coordinates/direction
- Delete it

The list numbering is based on the current obstacle list. If obstacles 1, 2, and 3 exist and obstacle 2 is deleted, the remaining list becomes 1 and 2. Adding another obstacle then correctly uses the next available displayed number.

## How the grid coordinate system works

The user-facing grid uses coordinates:

```text
X: 0–19
Y: 0–19
```

Internally, the Android grid uses a one-cell border and inverted screen-row positioning.

Conceptually:

```text
User X = internal column - 1
User Y = 19 - internal screen row
```

That conversion is why the visual grid can show Y=19 at the top while the internal canvas indexes rows from the opposite direction.

Each obstacle is stored locally as:

```text
[x, y]
```

Its direction is stored separately in the bearing table at the same coordinate.

The grid also maintains cell state such as:

- Unexplored
- Explored
- Obstacle
- Robot
- Image/identified obstacle

When an obstacle is moved or deleted, the old cell is returned to the unexplored state and the new cell is marked as an obstacle.

## How obstacles reach the RPi

The current communication flow is:

```text
Grid interaction
    ↓
GridMap obstacle list
    ↓
Send Obstacles button
    ↓
Home.printMessage()
    ↓
Existing Bluetooth connection
    ↓
RPi
```

The obstacle protocol is formatted as:

```text
OBSTACLE,<number>,<x grid cell>,<y grid cell>,<direction>
```

The grid coordinates are zero-based and remain in the range `0–19`; they are not converted to
centimetres when sent.

For example:

```text
OBSTACLE,1,5,12,N
```

After all obstacles are sent, the app sends:

```text
DONE
```

Task 1 and Task 2 also use the existing Bluetooth path:

- Begin task → `BEGIN`
- Stop task → `STOP`
- Move commands → `f`, `b`, `fl`, `fr`, `bl`, `br`
- Reset map → `CLEAR`

The Practice Tasks timer is local-only and does not communicate with the RPi.

## UI improvements I recommend

These can all be done without touching Bluetooth or RPi logic:

1. Add labels under every icon.

For example:

- Move Forward
- Turn Left
- Add Obstacle
- Save Map
- Load Map
- Reset Map

2. Add confirmation dialogs for destructive actions.

Especially:

- Reset Map
- Delete Obstacle
- Clear Bluetooth state

3. Show a visible mode indicator.

For example:

```text
Mode: Add Obstacle
Mode: Drag Obstacle
Mode: Change Obstacle
Mode: Set Start Point
```

This would make grid interaction much safer.

4. Disable incompatible modes automatically.

For example, enabling Drag should automatically disable:

- Add Obstacle
- Change Obstacle
- Set Start Point

The app already partly does this, but making it visually obvious would help.

5. Replace blank reset icons.

The reset image buttons currently have their icon source commented out, so they may appear empty. A visible reset icon with a label would be clearer.

6. Add an obstacle count.

For example:

```text
Obstacles: 4 / 20
```

7. Add a “Pending changes” indicator.

The app could visually show:

```text
Map changed — send obstacles to RPi
```

This would help distinguish local grid edits from data already transmitted.

8. Add a “Send successful” status.

The current Send Obstacles button updates the robot status to “Planning,” but a clearer status sequence would be:

```text
Sending obstacles…
Obstacles sent
Waiting for RPi…
Ready
```

That would improve confidence without changing the underlying Bluetooth behavior.

The safest design direction is to make the UI clearer around modes, confirmation, and communication status while leaving all existing Bluetooth commands and RPi message formats unchanged.
