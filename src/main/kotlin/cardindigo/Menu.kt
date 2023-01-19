package cardindigo

enum class Players{
    HUMAN, COMPUTER
}

class Menu(private val cards: Cards){
    fun execute(){
        println("Indigo Card Game")
        var hasSelected= false
        var selection: String =""
        /*
         If selection == yes, then the player plays first (Example 1), and if no, then the computer plays
          first (Example 2).
         */
        val selectedList = mapOf("YES" to Players.HUMAN, "NO" to Players.COMPUTER)
        var currentPlayer: Players = Players.COMPUTER
        var terminate = false
        while(!hasSelected){
            println("Play First?")
            selection = readln()
            if(selection.uppercase() in selectedList.keys){
                hasSelected = true
                currentPlayer = selectedList[selection.uppercase()]!!
            }else{
                if(selection.uppercase() == "EXIT"){
                    hasSelected = true
                    terminate = true
                }
            }

        }
        if(!terminate) {

            print("Initial cards on the table: ")
            cards.get(4)
            //put in unique 6 cards on players and computer's hand:
            cards.replenishHand(Players.HUMAN)
            cards.replenishHand(Players.COMPUTER)
            /*
        println("The number of cards on card deck = ${cards.cardDeckBeingUsed.size}")
        println("The number of cards on player hand = ${cards.cardsInPlayerHand.size}")
        println("The number of cards on computer hand= ${cards.cardsInComputerHand.size}")
        */

            //Here, we start the game
            var isPlaying = true
            while (isPlaying) {
                if (currentPlayer == Players.HUMAN) {
                    cards.showCardTableStatus()
                    var looper = -2 //feedback regarding status of loop
                    if (cards.cardDeckBeingUsed.isNotEmpty() || cards.cardsInPlayerHand.isNotEmpty()) {
                        while (looper != 0) {//A 0 means acceptable input from player.
                            //the looper provides a feedback mechanism
                            looper = cards.chooseCardToPlay(Players.HUMAN, looper)
                            if(looper == 1){
                                //this means that exit has been invoked:
                                isPlaying = false
                                looper = 0
                            }

                        }
                        currentPlayer = Players.COMPUTER
                    } else {
                        isPlaying = false
                    }

                } else {
                    cards.showCardTableStatus()
                    if (cards.cardDeckBeingUsed.isNotEmpty() || cards.cardsInComputerHand.isNotEmpty()) {
                        cards.chooseCardToPlay(Players.COMPUTER, 0)
                        currentPlayer = Players.HUMAN
                    } else {
                        isPlaying = false
                    }
                }
            }
        }
        println("Game Over")

    }
}

fun Cards.showCardTableStatus(){
    var message = "\n${this.cardsShownOnTheTable.size} cards on the table, "
    message += "and the top card is "
    message += this.cardsShownOnTheTable[this.cardsShownOnTheTable.size-1]
    println(message)
}

fun Cards.printCardsInHand(hand:Players){
    if(hand == Players.HUMAN){
        print("Cards in hand:")
        for(i in this.cardsInPlayerHand.indices){
            print(" ${i+1})${this.cardsInPlayerHand[i]}")
        }
        println()
    }
    if(hand == Players.COMPUTER){
        print("Cards in hand:")
        for(i in this.cardsInComputerHand.indices){
            print(" ${i+1})${this.cardsInComputerHand[i]}")
        }
        println()
    }

}
/** the player may choose a card or input exit */
fun Cards.chooseCardToPlay(hand:Players, loopStatus:Int): Int{
    if(hand == Players.HUMAN){
        if(cardsInPlayerHand.isEmpty()){
            this.replenishHand(hand)
        }
        if(loopStatus != -1) {//Do not print below if card selection is wrong
            this.printCardsInHand(hand)
        }
        val selections = 1..this.cardsInPlayerHand.size
        println("Choose a card to play (1-${this.cardsInPlayerHand.size}): ")
        val humanInput = readln()
        //below, the player will choose an exit and it will return with no error with code 1
        if(humanInput.uppercase() == "EXIT") return 1
        else {
            return try {
                val selected = humanInput.toInt()
                if(selected in selections){
                    val selectedCard = cardsInPlayerHand[selected-1]
                    this.cardsShownOnTheTable.add(selectedCard)
                    cardsInPlayerHand.remove(selectedCard)
                    0
                } else{
                    -1 //return with error and ask player again
                }
            } catch (e:Exception){
                -1
            }
        }


    }
    else{
        //computer selects a random card from its hand:
        if(cardsInComputerHand.isEmpty()){
            this.replenishHand(hand)
        }
        val selectedCard = cardsInComputerHand.shuffled()[0]
        println("Computer plays $selectedCard")
        cardsShownOnTheTable.add(selectedCard)
        cardsInComputerHand.remove(selectedCard)

        return 0
    }
}

fun Cards.replenishHand(hand: Players){
    if(this.cardDeckBeingUsed.isNotEmpty()) {

        if (hand == Players.HUMAN) {
            repeat(6) {
                this.reshuffle()
                val card = this.cardDeckBeingUsed[0]
                this.cardsInPlayerHand.add(card)
                this.cardDeckBeingUsed.remove(card)
            }
        }
        if (hand == Players.COMPUTER) {
            repeat(6) {
                this.reshuffle()
                val card = this.cardDeckBeingUsed[0]
                this.cardsInComputerHand.add(card)
                this.cardDeckBeingUsed.remove(card)
            }
        }
    }
}
fun Cards.reshuffle():Int{
    this.cardDeckBeingUsed = this.cardDeckBeingUsed.shuffled().toMutableList()
    return 0
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