package cardindigo

fun main() {
    val deck = CardDeck()
    deck.rank.forEach { print("$it ") }
    println()
    deck.suitCharacters.forEach { print("$it ") }
    println()
    deck.suit.forEach { print("$it ") }

}


class CardDeck{

    val rank:List<String> = generateRank()
    val suitCharacters =listOf('\u2666','\u2665','\u2660', '\u2663')
    private val switchedCharacters =listOf('\u2660','\u2665','\u2666','\u2663')
    val suit:List<String> = generateSuit()

    private fun generateSuit():List<String>{
        val cards:MutableList<String> = mutableListOf()

        for(i in switchedCharacters){
            rank.forEach { s->
                   cards.add("$i$s")
                }
        }


        return cards.toList()
    }


    private fun generateRank():List<String>{
        val initialList:List<String> = listOf("2","3","4","5","6","7","8","9","10")

        return mutableListOf<String>().also {
            it.addAll(initialList)
            it.add(0,"A")
            it.addAll(listOf("J", "Q","K"))
        }

    }



}
