package cardindigo

/** strategy
 * It is time to make the computer smarter. Until now, the computer played a random card, but now it will follow a certain strategy.

For testing purposes, print the computer cards in hand before the computer chooses a card.

From now on, we will introduce the term candidate cards. They are the cards in hand that can win the cards on the table.

The  AI strategy is as follows: (From the rules)

1) If there is only one card in hand, put it on the table (Example 2);

2) If there is only one candidate card, put it on the table (Example 3);

3) If there are no cards on the table:

If there are cards in hand with the same suit, throw one of them at random (Example 4).
For example, if the cards in hand are 7♥ 9♥ 8♣ A♠ 3♦ 7♦ Q♥ (multiple ♥, and ♦ suits),
the computer will play one card at random.
If there are no cards in hand with the same suit, but there are cards with the same rank
(this situation occurs only when there are 4 or fewer cards in hand), then throw one of
them at random (Example 5). For example, if the cards in hand are 7♦ 7♥ 4♠ K♣, throw
one of 7♦ 7♥ at random.
If there are no cards in hand with the same suit or rank, throw any card at random.
For example, if the cards in hand are 9♥ 8♣ A♠ 3♦, throw any of them at random.

4) If there are cards on the table but no candidate cards, use the same tactics as
in step 3. That is:

If there are cards in hand with the same suit, throw one of them at random (Example 6).
For example, if the top card on the table is A♦, and the cards in hand are
6♣ Q♥ 8♣ J♠ 7♣ (multiple ♣ suit), the computer will place any of 6♣ 8♣ 7♣ at random.
If there are no cards in hand with the same suit, but there are cards with the same
rank (this may occur when there are 3 or fewer cards in hand), throw one of them at
random (Example 7). For example, if the top card on the table is A♦ and the cards in
hand are J♠ Q♥ J♣, put one of J♠ J♣ at random.
If there are no cards in hand with the same suit or rank, then put any card at random.
For example, if the top card on the table is A♦, and the cards in hand are J♠ Q♥ K♣,
throw any of them at random.


5) If there are two or more candidate cards:

If there are 2 or more candidate cards with the same suit as the top card on the table,
throw one of them at random (Example 8). For example, if the top card on the table is 5♥,
and the cards in hand are 6♥ 8♣ 5♠ 7♦ 7♥, then the candidate cards are 6♥ 7♥ 5♠. There are
2 candidate cards with the same suit as the top card on the table, 6♥ 7♥. Place any at random.
If the above isn't applicable, but there are 2 or more candidate cards with the same rank
as the top card on the table, throw one of them at random (example 9). For example, if
the top card on the table is J♥, and the cards in hand are 3♥ J♣ J♠ 6♦, then the candidate
cards are 3♥ J♣ J♠. In this case, there are no 2 or more candidate cards with the same suit,
but there are 2 candidate cards with the same rank as the top card on the table that
are J♣ J♠. Put any at random.
If nothing of the above is applicable, then throw any of the candidate cards at random.
The strategy above can be improved. Moreover, the computer can play even better if it
keeps track of the cards that have been played. However, our goal isn't a complex
algorithm for the computer, but to learn how to apply the strategy.
 */


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






