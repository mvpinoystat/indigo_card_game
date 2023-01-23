package cardindigo

class Cards{

    companion object {
        val suitCharacters =listOf('\u2660','\u2665','\u2666','\u2663')

        fun generateRank():List<String>{
            val initialList:List<String> = listOf("2","3","4","5","6","7","8","9","10")

            return mutableListOf<String>().also {
                it.addAll(initialList)
                it.add(0,"A")
                it.addAll(listOf("J", "Q","K"))
            }

        }
    }

    private val rank:List<String> = generateRank()
    //val cardDeckReference: List<String> = generateSuit().shuffled()
    var cardDeckBeingUsed: MutableList<String>  = generateSuit().toMutableList()
    var cardsShownOnTheTable: MutableList<String> = mutableListOf()

    private fun generateSuit():List<String>{
        val cards:MutableList<String> = mutableListOf()

        for(i in suitCharacters){
            rank.forEach { s->
                cards.add("$s$i")
            }
        }


        return cards.toList()
    }

}

/* Start of Extension Functions */

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
            printCardsInHand(hand)
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
                        if(checkSelectedCardIfWinner(selectedCard, this.cardsShownOnTheTable.last()))
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
        /**This is where the computer use Logic to select a card **/
        //print the cards:
        hand.cardsOnHand.forEach {
            print("$it ")
        }
        println()
        //compute logic to select the card:
        val selectedCard = ComputerAI(hand.cardsOnHand, this.cardsShownOnTheTable)
            .applyThisRuleIfThereIsOnlyOneCardOnHand() //rule 1
            .applyThisRuleIfThereIsOneCandidateCard() //rule 2
            .applyRules3And4and5() //rule 3, 4 and 5
            .returnSelectedCard()
        println("Computer plays $selectedCard")
        if(this.cardsShownOnTheTable.isNotEmpty()){
            if(checkSelectedCardIfWinner(selectedCard, this.cardsShownOnTheTable.last()))
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
