package cardindigo


const val RETURN_NORMAL = 0
const val RETURN_ERROR = -1
const val RETURN_EXIT_GAME = 1
const val RETURN_WIN = 2
const val CONTINUE_LOOP = -2
const val THREE_POINTS = 3

enum class CardGamers{
    Player, Computer
}

//a class of player:
class Player(val kind: CardGamers){
    val cardsOnHand = mutableListOf<String>()
    var score = 0
    val cardsWon = mutableListOf<String>()
}

class Menu(private val cards: Cards){
    //two types of players initialized
    private val human = Player(CardGamers.Player)
    private val computer = Player(CardGamers.Computer)

    fun execute(){
        println("Indigo Card Game")
        var hasSelected= false
        var selection: String
        /*
         If selection == yes, then the player plays first (Example 1), and if no, then the computer plays
          first (Example 2).
         */
        val selectedList = listOf("YES" , "NO" )
        var currentPlayer: Player = computer
        var terminate = false
        while(!hasSelected){
            println("Play First?")
            selection = readln()
            if(selection.uppercase() in selectedList){
                hasSelected = true
                currentPlayer = if(selection.uppercase() == "YES") human else computer
            }else{
                if(selection.uppercase() == "EXIT"){
                    hasSelected = true
                    terminate = true
                }
            }

        }
        val firstPlayer = currentPlayer
        var currentWinningPlayer = currentPlayer
        if(!terminate) {

            print("Initial cards on the table: ")
            cards.get(4)
            //put in unique 6 cards on players and computer's hand:
            cards.replenishHand(human)
            cards.replenishHand(computer)

            //Here, we start the game
            var isPlaying = true
            var looper : Int
            var displayLastStatusFlag = true//this
            while (isPlaying) {
                cards.showCardTableStatus()
                if (currentPlayer.kind == CardGamers.Player) {
                    looper = CONTINUE_LOOP //feedback regarding status of loop
                    if (cards.cardDeckBeingUsed.isNotEmpty() || human.cardsOnHand.isNotEmpty()) {
                        while (looper != RETURN_NORMAL ) {//A 0 means acceptable input from player.
                            //the looper provides a feedback mechanism
                            looper = cards.chooseCardToPlay(human, looper)
                            if(looper == RETURN_EXIT_GAME){
                                //this means that exit has been invoked:
                                isPlaying = false
                                looper = RETURN_NORMAL
                                displayLastStatusFlag = false
                            }
                            if(looper == RETURN_WIN){
                                printGamePlayersStatus(human, computer, human, true)
                                looper = RETURN_NORMAL
                                currentWinningPlayer = currentPlayer
                            }

                        }
                        currentPlayer = computer
                        continue //next while session
                    } else {
                        isPlaying = false
                    }

                }
                else { // computer player
                    if (cards.cardDeckBeingUsed.isNotEmpty() || computer.cardsOnHand.isNotEmpty()) {
                        looper = cards.chooseCardToPlay(computer, 0)
                        if(looper == RETURN_WIN){
                            printGamePlayersStatus(human, computer, computer, true)
                            currentWinningPlayer = currentPlayer
                        }
                        currentPlayer = human
                        continue //next while session
                    } else {
                        isPlaying = false
                    }
                }
            }

            //the play is terminated. Now, if there are still remaining cards on table but
            //the players have no more cards:, the points will transfer to the last player who
            //won the last card
            cards.transferCardsToPlayer(currentWinningPlayer)
            //now whoever gets the most card, wins the three points:

            if (human.cardsWon.size > computer.cardsWon.size) {
                human.score += THREE_POINTS
            } else{
                if(human.cardsWon.size < computer.cardsWon.size){
                    computer.score += THREE_POINTS
                } else{
                    //if the quantity of cards won are equal:
                    firstPlayer.score += THREE_POINTS
                }
            }



            if(displayLastStatusFlag) printGamePlayersStatus(human,computer,human,false)

        }

        println("Game Over")

    }
}