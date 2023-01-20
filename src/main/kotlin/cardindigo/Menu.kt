package cardindigo

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
    //two types of players initialize
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
            var looper = CONTINUE_LOOP
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
                                cards.printGamePlayersStatus(human, computer, human, true)
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
                    looper = CONTINUE_LOOP
                    if (cards.cardDeckBeingUsed.isNotEmpty() || computer.cardsOnHand.isNotEmpty()) {
                        looper = cards.chooseCardToPlay(computer, 0)
                        if(looper == RETURN_WIN){
                            cards.printGamePlayersStatus(human, computer, computer, true)
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
            //now whoever gets the most card, wins:
            when{
                human.cardsWon.size > computer.cardsWon.size -> human.score += THREE_POINTS
                human.cardsWon.size < computer.cardsWon.size-> computer.score += THREE_POINTS
                else -> firstPlayer.score += THREE_POINTS
            }

            if(displayLastStatusFlag) cards.printGamePlayersStatus(human,computer,human,false)

        }

        println("Game Over")

    }
}

fun Cards.printGamePlayersStatus(humanPlayer: Player, computerPlayer:Player, won: Player,
printWhoWins: Boolean){
    if(printWhoWins) println("${won.kind.name} wins cards")
    var line2 = "Score: ${humanPlayer.kind.name} ${humanPlayer.score}"
    line2 += " - ${computerPlayer.kind.name} ${computerPlayer.score}"
    println(line2)
    var line3 = "Cards: ${humanPlayer.kind.name} ${humanPlayer.cardsWon.size}"
    line3 += " - ${computerPlayer.kind.name} ${computerPlayer.cardsWon.size}"
    println(line3)


}

fun Cards.showCardTableStatus(){
    if(this.cardsShownOnTheTable.isNotEmpty()){
        var message = "\n${this.cardsShownOnTheTable.size} cards on the table, "
        message += "and the top card is "
        message += this.cardsShownOnTheTable[this.cardsShownOnTheTable.size-1]
        println(message)
    }
    else{
        println("\nNo cards on the table")
    }
}

fun Cards.printCardsInHand(hand:Player){
    if(hand.kind == CardGamers.Player){
        print("Cards in hand:")
        for(i in hand.cardsOnHand.indices){
            print(" ${i+1})${hand.cardsOnHand[i]}")
        }
        println()
    }
    if(hand.kind == CardGamers.Computer){
        print("Cards in hand:")
        for(i in hand.cardsOnHand.indices){
            print(" ${i+1})${hand.cardsOnHand[i]}")
        }
        println()
    }

}
/** the player may choose a card or input exit */
/** stage 4/5: The chosen card will be matched to the topmost card and decide
 * if the current player win
 */
fun Cards.chooseCardToPlay(hand:Player, loopStatus:Int): Int{
    if(hand.kind == CardGamers.Player){
        if(hand.cardsOnHand.isEmpty()){
            this.replenishHand(hand)
        }
        if(loopStatus != RETURN_ERROR) {//Do not print below if card selection is wrong
            this.printCardsInHand(hand)
        }
        val selections = 1..hand.cardsOnHand.size
        println("Choose a card to play (1-${hand.cardsOnHand.size}): ")
        val humanInput = readln()
        //below, the player will choose an exit, and it will return with no error with code 1
        if(humanInput.uppercase() == "EXIT") return RETURN_EXIT_GAME
        else {
            return try {
                val selected = humanInput.toInt()
                if(selected in selections){
                    val selectedCard = hand.cardsOnHand[selected-1]
                    //compare cards:
                    if(this.cardsShownOnTheTable.isNotEmpty()) {
                    if(this.checkSelectedCardIfWinner(selectedCard, this.cardsShownOnTheTable.last()))
                         {
                            this.cardsShownOnTheTable.add(selectedCard)
                            hand.cardsOnHand.remove(selectedCard)
                            return this.transferCardsToPlayer(hand)

                        } else {
                            this.cardsShownOnTheTable.add(selectedCard)
                            hand.cardsOnHand.remove(selectedCard)
                        }
                    }
                    else{
                        this.cardsShownOnTheTable.add(selectedCard)
                        hand.cardsOnHand.remove(selectedCard)
                    }
                    RETURN_NORMAL
                } else{
                    RETURN_ERROR //return with error and ask player again
                }
            } catch (e:Exception){
               RETURN_ERROR
            }
        }


    }
    else{
        //computer selects a random card from its hand:
        //below hand is the computer hand
        if(hand.cardsOnHand.isEmpty()){
            this.replenishHand(hand)
        }
        val selectedCard = hand.cardsOnHand.shuffled()[0]
        println("Computer plays $selectedCard")
        if(this.cardsShownOnTheTable.isNotEmpty()){
            if(this.checkSelectedCardIfWinner(selectedCard, this.cardsShownOnTheTable.last()))
            {
                this.cardsShownOnTheTable.add(selectedCard)
                hand.cardsOnHand.remove(selectedCard)
                return this.transferCardsToPlayer(hand)
            }
            else{
                this.cardsShownOnTheTable.add(selectedCard)
                hand.cardsOnHand.remove(selectedCard)
            }
        }
        else{
            this.cardsShownOnTheTable.add(selectedCard)
            hand.cardsOnHand.remove(selectedCard)
        }
        return RETURN_NORMAL
    }
}

fun Cards.checkSelectedCardIfWinner(selected: String, topMostCard: String): Boolean {
   //check if 10 is there:
    val truthTable = mutableListOf(false, false, false)
   if(selected.length == 3 && topMostCard.length == 3) truthTable[0] = true
   if(selected[0] == topMostCard[0]) truthTable[1] = true
   if(selected.last() == topMostCard.last()) truthTable[2] = true

   return true in truthTable
}

fun Cards.transferCardsToPlayer(player: Player): Int{
    player.score += this.computeScore()
    //put the cards on computer's hand:
    this.cardsShownOnTheTable.forEach {
        player.cardsWon.add(it)
    }
    //empty the cards on table:
    this.cardsShownOnTheTable.clear()
    return RETURN_WIN
}

fun Cards.replenishHand(hand: Player){
    if(this.cardDeckBeingUsed.isNotEmpty()) {

        if (hand.kind == CardGamers.Player) {
            repeat(6) {
                this.reshuffle()
                val card = this.cardDeckBeingUsed[0]
                hand.cardsOnHand.add(card)
                this.cardDeckBeingUsed.remove(card)
            }
        }
        if (hand.kind == CardGamers.Computer) {
            repeat(6) {
                this.reshuffle()
                val card = this.cardDeckBeingUsed[0]
                hand.cardsOnHand.add(card)
                this.cardDeckBeingUsed.remove(card)
            }
        }
    }
}
fun Cards.reshuffle():Int{
    this.cardDeckBeingUsed = this.cardDeckBeingUsed.shuffled().toMutableList()
    return 0
}

fun Cards.computeScore():Int {
    var score = 0
    val pattern = Regex("10")
    this.cardsShownOnTheTable.forEach {
        var points = 0
        if(it[0] in listOf('A','J','Q','K')) points = 1
        if(pattern.containsMatchIn(it)) points = 1

        score += points
        //println("${it[0]} : score: $points")
    }
    return score
}

fun Cards.reset():Int{
    this.cardDeckBeingUsed = this.cardDeckReference.shuffled().toMutableList()
    return 0
}
// Put a card on the table:
fun Cards.putInTable(card:String){
    for(i in this.cardDeckBeingUsed){
        if(card == i){
            this.cardsShownOnTheTable.add(card)
        }
    }

}

fun Cards.showCardsOnTable(){
    cardsShownOnTheTable.forEach{
        print("$it ")
    }
    println()
}
//get N amount of cards:
fun Cards.get(input:Int):Int{
    val theRange = 1..this.cardDeckBeingUsed.size
    return when(input){
        in theRange -> {
            if(this.cardDeckBeingUsed.size < input){
                println("The remaining cards are insufficient to meet the request.")
                return -1
            }
            else{
                this.reshuffle()
                val indexStart = this.cardDeckBeingUsed.size - input
                //add the cards:
                for(i in indexStart until this.cardDeckBeingUsed.size){
                   this.cardsShownOnTheTable.add(this.cardDeckBeingUsed[i])
                }
                //remove cards from deck:
                for(i in cardsShownOnTheTable){
                    if(i in this.cardDeckBeingUsed) this.cardDeckBeingUsed.remove(i)
                }
                //print it out:
                cardsShownOnTheTable.forEach{
                    print("$it ")
                }
                println()
                return 0
            }

        }
        else -> {
            println("Invalid number of cards.")
            -1
        }
    }


}