=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 1200 Game Project README
PennKey: philgao
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. 2D Array: A Tetris "board" is 10 squares wide by 20 squares high. The board
  is of a 2D array, each containing a Block that is either filled in with a specific
  color or blank (in other words, the Block's color variable is either an integer from
  1-7 or 0). I've used a 2D array for the board because it allows me to map the precise
  position of each block with x and y coordinates (which correspond to the columns and
  rows of the 2D array, respectively). A blank square will be any area where there are no
  blocks. Any square that contains a block will be filled in with the specific color of
  that block. The 2D array also helped me keep track of where there are blocks with color,
  and where there are blocks with no color, which has helped me implement object
  detection/boundaries. When the game starts, all squares on the board are white,
  representing a blank board (each Block's color variable has an integer value of 0).

  2. Collections/Maps: I used a TreeMap called "moves" to store information containing all
  the moves that have been made by the user. The key in the TreeMap corresponds to the piece
  that landed during that move, and each individual value stores the board state (2D array of
  blocks) of the game after the piece landed. The TreeMap resizes after a piece has landed
  to add that piece to the map along with the corresponding board after its landing. Using
  the TreeMap, I was able to implement unlimited undo functionality. Pressing undo enough
  times will take the user back to the start of the game, where the current piece and next
  piece are exactly the same as the start. I chose to use a TreeMap instead of a Set or a
  Hashmap because the order of moves matters. Using the TreeMap data structure ensures that
  the values are sorted in a natural order or a defined order using the Comparator (my definition
  of order checked the "order" integer variable that each piece contained, which then sorted the
  TreeMap by ascending order values). Because of this ordered functionality, I can access the
  moves accordingly and remove the move from the moves TreeMap if the undo() method is called.

  3. File I/O: I used File I/O to store the game state. This functionality allows
  the user to save the current game, quit the game, come back at a later time, and load the
  saved game. When the save button is pressed, the 2D array of blocks will be saved in a text file.
  Furthermore, a few extra lines of information regarding the current piece being dropped,
  the next piece, the level, number of lines cleared, and the score is saved. Then, when
  loading the saved game, my code will read the text file and restore the original state of the
  board, along with the other components that I listed above. The writing and parsing of the
  game state is done by the GameWriter and GameParser classes.

  4. JUnit Testable Component: I have tested numerous components of my game to ensure that
  it is working properly. The main state of my game is the board (2D array) that can be
  tested with methods that I created in the GameCourt class. I've also created custom
  getter methods to get copies of the board, piece, nextPiece, level, lines, playing, and score
  variables. Furthermore, I've added two setter methods to modify the board and piece to create
  custom scenarios to test my functionality.

  As for what I tested, I made sure that my object detection/collision system worked properly
  so that when a piece starts dropping, it will stop once it has collided with an existing
  block that has already been placed on the board or the border of the board. I have also
  tested line clearing. After an entire row of squares has been filled with blocks, I checked
  that my program correctly removes all blocks from that row and shifts all blocks above that
  line down one row. I also tested basic functionality like my createPiece(), start(), clear(),
  pause(), undo(), and tick() methods to ensure that they were working properly.

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.

  Block: The Block class represents a singular block that is drawn on the board or a singular
  block that belongs to a piece. This class is integral to how the Tetris game is drawn and
  how it operates. A Block contains a single variable that keeps track of its color. The value
  of the variable can be any integer from 0 to 7 (where 0 is white, and 1-7 are colors of
  different Tetris pieces). The variable is final since once a Block is created, it cannot be
  modified (if you want to change the color, a new Block should be created to take its place).
  The Block class also has a translateColor() method to translate the integer to a Color that
  can be used to for drawing.

  Game: The Game class is called when the User would like to run the game. It has a main method
  that creates a new instance of the RunTetris class, which starts the game.

  GameCourt: The GameCourt class is where the main functionality of the game is stored. It handles
  everything from creating and updating the board to saving the game. Some key methods that the
  GameCourt class contains include createPiece(), start(), reset(), clear(), undo(), pause(), and
  save(). The GameCourt also dictates how the game is drawn with the paintComponent() method.
  For more information on the methods themselves, refer to the comments above the methods in the
  GameCourt class.

  GameParser: GameParser is a class that takes in a csv file that contains information for a
  saved game and converts the file into data that then updates the current game. When the start()
  method is called, a new GameParser is created and takes information from the file
  "saved_game.csv" and converts the data into the board, piece, nextPiece, level, lines, and score
  variables. Then, the variables of the GameCourt are updated to contain the values parsed from the
  save file.

  GameWriter: GameWriter is a class that takes the current game and saves its information into
  a file to be parsed when the program is run again. The GameWriter takes the board, piece, nextPiece,
  level, lines, and score and writes Strings into the file that the GameParser class can interpret
  when the game is run again.

  Piece: The Piece class is a superclass that provides general functionality and properties for
  each Tetris piece. Each piece has position variables, velocity variables, a 2D array of blocks,
  color, boundaries for the court, and an order that corresponds to their order in the moves TreeMap.
  The Piece class also provides methods like move(), intersectsWall(), intersectsBoundary(),
  rotateCW(), and ifRotateCW() that control Piece movement and rotation.

  PlaySound: The PlaySound class handles all the sound operations throughout the game. Firstly,
  when the PlaySound class is created, it accesses the .wav files for the music and sound effects
  and creates Clips that can be played, stopped, and looped. The PlaySound class contains four
  private variables of type Clip: music, landEffect, clearEffect, and tetrisEffect. Each Clip
  contains methods that control playing and pausing the music or sound effect.

  RunTetris: RunTetris is the Game Main class that specifies the frame and widgets of the GUI.
  This class remained almost unchanged from the sample code.

  Classes in pieces folder (BoxPiece/IPiece/LPiece1/LPiece2/TPiece/ZPiece1/ZPiece2):
  Each piece within the "pieces" folder is a subclass of the Pieces class. The individual pieces
  contain the same functionality and all the same methods as the Piece superclass. However, each
  piece defines a new value for the Piece class's int color and Block[][] blocks. Although the
  subtyping in my project doesn't satisfy the requirements for the "Inheritance and subtyping"
  concept, it's a good practice of organization and helps me keep track of the different pieces
  that I am creating.

- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?

  Detecting current pieces on the board and doing piece collision was one of the most challenging
  aspects of the game implementation. Sometimes the piece would collide when it was nowhere near
  the current blocks on the board. Other times, the dropped piece wouldn't detect the blocks on
  the board at all and would keep moving down until it moved off the screen. I figured out that a
  lot of my problems were caused by a simple issue: I flipped the rows and columns in my 2D arrays
  of blocks. I became confused by what the variable names i and j meant in my for loops, so I
  mistakenly flipped them in a few places. To clear this up, I changed all of my for loops to contain
  descriptive variable names for the indexes (row and col). After that I correctly matched my indexes
  to their respective row and col spots, my piece collision finally worked.

  Additionally, I was challenged with making sure the piece couldn't rotate for edge cases. For example,
  I had to ensure that the Piece could not rotate outside the boundary of the 10 block wide by 20 block
  high board. If I allowed that rotation to happen, then it would result in an index out of bounds error
  (because you cannot access a block that is less than 0 in the row or col or greater than the row and col
  boundaries). Therefore, I created a method called ifRotateCW() within the Piece class that would ensure
  that the piece would not rotate if it would cause it to go off the board. The functionality was simple:
  if the piece returned by the ifRotateCW() class would result in blocks that are outside the boundary,
  the piece would not be allowed to rotate.

  Finally, implementing undo() was a significant stumbling block that I had to overcome. The undo method had
  to make the board, piece, nextPiece, level, lines, and score return to their values from the previous move.
  Although undoing the actual pieces themselves was easy, making the game board, level, lines, and score undo
  to their original state was a major challenge. The value of the moves TreeMap was the board state. However,
  it was the board state after the previous piece landed. Therefore, I had to remove the piece from the board, and
  somehow perform calculations with the level, lines, and score to change their values accordingly. I ended up
  doing some calculations and found a simple formula that would give the correct level, lines, and score
  values of the previous move. I also updated the board so that it contained the block values of the previous
  board minus the blocks where the previous piece landed.

- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?

  My functionality is well-separated between classes and methods. I made sure that my file I/O was
  properly separated from the GameCourt by creating a class for both writing and parsing a file. I
  made sure that each subtype of the Piece class was a different class, and I neatly stored them in
  a folder called "pieces." I separated my functionality for playing sounds by once again creating
  a new class called PlaySound to handle all music and sound effects that I had in my game.

  Even within the GameCourt Class (which is the class that contains most of my game functionality),
  I separate my functionality by methods. I use methods for creating new pieces (createPiece), starting
  the game (start), resetting the game to get a clear board (reset), clearing row(s) of blocks (clear),
  undoing the previous action (undo), pausing the game (pause), and saving the game (save).

  As for my private state encapsulation, I ensured that each class contained private
  variables, with only methods and immutable variables like COURT_HEIGHT, VELOCITY, or
  SCALE being accessible publicly. I also made sure that methods like getBlocks() returned
  a copy of the data structure, instead of a reference to the original blocks 2D array. For
  primitive data types like integers, I could just return the variable since they are stored
  on the stack.

  However, there are still a couple areas of my code that I could potentially refactor. Firstly
  I could create methods that help with drawing different components of the game onto the screen,
  instead of having paintComponent handle almost everything (I already do that with my pieces. Each piece
  that I instantiate has a method that handles drawing itself). I could also move some of my methods
  from GameCourt into other files so that the class isn't as long and bulky.

========================
=: External Resources :=
========================

- Cite any external resources (images, tutorials, etc.) that you may have used 
  while implementing your game.

  I used a YouTube tutorial when implementing the sounds in my code:
  https://www.youtube.com/watch?v=3q4f6I5zi2w

  Here is where I got the Tetris logo:
  https://tetris.com/brand-assets

  Here is where I got the sounds for my game:
  tetris song: https://archive.org/details/TetrisThemeMusic
  land, clear, and tetris effects: https://www.zophar.net/music/nintendo-nes-nsf/tetris-1989-Nintendo

