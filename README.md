## TicTacToe Server - Milestone 1

This milestone set up the basic structure for the TicTacToe Server. I created all the needed storage classes (like User and Event), 
defined the message packages (Request and Response) for sending info over the network, and built the main control classes (SocketServer, ServerHandler).
With all of this done, the project will soon be able to have an actual connection and game logic.

## TicTacToe Server - Milestone 2 Report
* Do you have a working System that allows two players to play TicTacToe once?
  - No, the system is not working properly yet. There are some issues with the server. When the first player send a move, the second player does not receive that move and then both players are stuck in "Waiting for Opponent"

* Explain how the game uses the attribute turn and player of the TicTacToe class to manage the game state between the two distributed game boards. Can we still achieve the same thing without introducing player attributes in this milestone? Why?
  - The distributed game state is managed successfully using a combination of the server's shared static Event object and the client's local TicTacToe attributes (turn and player)
  - No, we could not achieve the same turn-taking logic without introducing the local player attribute in this milestone.
 
* The server currently deletes or resets the game move once it sends a GamingResponse to the client. Is this necessary? What are the pros and cons?
  - The server currently resets the shared event.move to -1 immediately after successfully sending the move inside handleRequestMove().
  - Yes, it is necessary for the current client-polling architecture because it prevents a double move which is a pro and it's inefficient, which is a con.

* The ButtonHandler inner class in MainActivity currently loops through all 9 buttons to know which button is clicked. Can you think of a way to not loop through all the buttons? You can implement it to support your explanation, but it is not compulsory.
  - By setting a unique ID and retrieving it directly


## TicTacToe Server - Milestone 3 Report
* What is the difference between COMPLETE_GAME and ABORT_GAME
requests?
  - A COMPLETE_GAME request ends a game normally by marking its status as COMPLETED, 
while an ABORT_GAME request ends it prematurely by marking it as ABORTED. 
  - In both cases, the active event is closed and the current event ID is reset.

* With the current implementation, can two users login to the system using the
same account credentials? Why or Why not?
  - Yes, two users can log in with the same credentials in the current implementation, because nothing in the provided code enforces a limit of one active session per account.
    There is no session tracking, no check for an existing logged-in user, and no mechanism to block additional logins for the same username/password.

* Explain the stages of EventStatus and object Event class goes through in the
game process. Use the “The Flow of Game Event” diagram above as a guide.
  - An Event object starts in the PENDING status when an invitation is sent, 
  then transitions to either ACCEPTED or DECLINED based on the recipient's response. 
  If accepted, the event moves through PLAYING (when acknowledgment is received) and 
  ends in either COMPLETED (when the game finishes normally) 
  or ABORTED (if the game is terminated early or a socket disconnects).



