# Snake-game-Java
Multithreaded application which implements the basic Snake game using Java and Swing.

# Application operation


#
<ul>
<li>multithreaded implementation of game logic and graphical interface</li>
<li>actual gameplay happens in a separate thread independent of the user interface thread</li>
<li>all game elements, such as the game tick, food consumption, collision with walls or own segment, generates an event that is received by appropriate listeners</li>
<li>JTable is used as main graphical representation of the game board, with implementation of TableCellRenderer and AbstractTableModel</li>
<li>file saving system with storing information about the highest score gained</li>
</ul>

#