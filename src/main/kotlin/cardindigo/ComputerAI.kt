package cardindigo


class ComputerAI(private val cardsOnHand: List<String>,
private val cardsOnTheTable: List<String>) {
    private var selectedCard: String = cardsOnHand.shuffled()[0]//default to random
    fun returnSelectedCard(): String {
        return selectedCard
    }

    /**Rule 1:
    If there is only 1 card in hand, put it on the table:
     */
    //another pattern
    fun applyThisRuleIfThereIsOnlyOneCardOnHand() = apply {
        if (cardsOnHand.size == 1) {
            //println("Rule 1 applied.")
            selectedCard = cardsOnHand[0]
        }
    }

    /** Rule 2: if there is only one candidate card, put it on the table.
     * A candidate card is a card that will enable the computer to win
     * the game.
     */
    //another type of pattern:
    fun applyThisRuleIfThereIsOneCandidateCard(): ComputerAI {
        if(cardsOnTheTable.isNotEmpty()) {

            val topCard = cardsOnTheTable.last()
            //check how many candidate card:
            val candidateCards = mutableListOf<String>()
            cardsOnHand.forEach { z ->
                if (checkSelectedCardIfWinner(z, topCard))
                    candidateCards.add(z)
            }
            if (candidateCards.size == 1) {
                selectedCard = candidateCards[0]
                //println("Rule 2 applied.")
            }
        }

        return this
    }

    /** Rule 3: If there are no cards on the table. This means that
     * cardsOnTheTable is empty.
     * 1. Check if we have cards in hand with the same suits(same heart, spade etc).
     * If there are, pick this card at random.
     * 2. If there are no cards in hand with the same suit, but there are cards with the same
     * rank , select one of them randomly.
     *
     * Here there is Rule 4 if cards on the table are not empty
     */
    fun applyRules3And4and5() = apply {

        if (cardsOnTheTable.isEmpty()) {
           selectedCard = rule3tactics()

        } else { //rule 4 starts here:
            val topCard = cardsOnTheTable.last()
            //check how many candidate card:
            val candidateCards= mutableListOf<String>()
            cardsOnHand.forEach() {
                if(checkSelectedCardIfWinner(it, topCard))
                    candidateCards.add(it)
            }
            if(candidateCards.isEmpty()){
                //printRuleStatus("**Under Rule 4**")
                selectedCard = rule3tactics()
            }
            if(candidateCards.size > 1){
                //rule 5 starts here: //
                val cardsWithSameSuitAsTopCard = candidateCards.filter {
                    it.last() == topCard.last()
                }
                if(cardsWithSameSuitAsTopCard.size > 1) {
                    //printRuleStatus("5-1")
                    selectedCard = cardsWithSameSuitAsTopCard.shuffled()[0]
                }
                else{
                    val topCardRank = topCard.toCharArray().dropLast(1).toString()
                    //show only the ranks in the cards on Hand//remove the suit symbols
                    val rankOfCardsOnHand = cardsOnHand.map {
                        it.toCharArray().dropLast(1).toString()
                    }
                    //get the indices of cardsOnHand with similar rank as the topCard:
                    val indicesOfRank = mutableListOf<Int>()
                    for (i in rankOfCardsOnHand.indices) {
                        if(rankOfCardsOnHand[i] == topCardRank) indicesOfRank.add(i)
                    }
                    //implement rule 5-2
                    if(indicesOfRank.size > 1){
                        //printRuleStatus("5-2")
                        selectedCard = cardsOnHand[indicesOfRank.shuffled()[0]]
                    }
                    else{
                        //implement rule 5-3
                        //printRuleStatus("5-3")
                        selectedCard = candidateCards.shuffled()[0]
                    }

                }
            }


        }

    }
    /**
    private fun printRuleStatus(rule:String){
        //println("Applied rule $rule")
    }
    */

    //returns the selected card:
    private fun rule3tactics():String{
        val indicesOfCardsOnHandWithSameSuit = returnIndexOfSimilarSuitCards(cardsOnHand)
        if(indicesOfCardsOnHandWithSameSuit.isNotEmpty()) {
            //Rule 3-1
            //pick card at random :
            //printRuleStatus("3-1")
            return cardsOnHand[indicesOfCardsOnHandWithSameSuit.shuffled()[0]]
        }
        else{
            val indicesOfCardsOnHandWithSameRank = returnIndexOfSimilarRank(cardsOnHand)
            if(indicesOfCardsOnHandWithSameRank.isNotEmpty()) {
                //Rule 3-2
                //printRuleStatus("3-2")
                //pick a card at random:
                return cardsOnHand[indicesOfCardsOnHandWithSameRank.shuffled()[0]]
            }
            else{
                //Rule 3-3
                //printRuleStatus("3-3")
                return cardsOnHand.shuffled()[0]
            }
        }

    }


    private fun returnIndexOfSimilarSuitCards(onHand:List<String>):List<Int>{
        val similarSuitIndex = mutableListOf<Int>()
        for (i in onHand.indices) {
           for (j in onHand.indices){
               if(i != j){
                   if(onHand[i].last() == onHand[j].last()){
                      similarSuitIndex.add(i)

                   }
               }
           }
       }
        return similarSuitIndex
    }

    private fun returnIndexOfSimilarRank(onHand: List<String>):List<Int>{
        val similarRankIndex = mutableListOf<Int>()
        //remove the suit codes:
        val ranksOnHand = onHand.map{ itz ->
           val stripped = itz.toCharArray()
           stripped.dropLast(1).toString()
        }
        for (i in ranksOnHand.indices) {
            for (j in ranksOnHand.indices) {
                if(i != j){
                    if(ranksOnHand[i] == ranksOnHand[j])
                        similarRankIndex.add(i)
                }
            }
        }
        return similarRankIndex
    }


}






