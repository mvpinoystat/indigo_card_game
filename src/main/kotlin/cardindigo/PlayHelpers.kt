package cardindigo

fun printCardsInHand(hand:Player){
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

fun checkSelectedCardIfWinner(selected: String, topMostCard: String): Boolean {
    //check if 10 is there:
    val truthTable = mutableListOf(false, false, false)
    if(selected.length == 3 && topMostCard.length == 3) truthTable[0] = true
    if(selected[0] == topMostCard[0]) truthTable[1] = true
    if(selected.last() == topMostCard.last()) truthTable[2] = true

    return true in truthTable
}

fun printGamePlayersStatus(humanPlayer: Player, computerPlayer:Player, won: Player,
                           printWhoWins: Boolean){
    if(printWhoWins) println("${won.kind.name} wins cards")
    var line2 = "Score: ${humanPlayer.kind.name} ${humanPlayer.score}"
    line2 += " - ${computerPlayer.kind.name} ${computerPlayer.score}"
    println(line2)
    var line3 = "Cards: ${humanPlayer.kind.name} ${humanPlayer.cardsWon.size}"
    line3 += " - ${computerPlayer.kind.name} ${computerPlayer.cardsWon.size}"
    println(line3)


}


