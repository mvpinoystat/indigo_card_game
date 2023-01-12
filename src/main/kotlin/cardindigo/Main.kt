package cardindigo

fun main() {
    Menu(Cards()).execute()
}


class Cards{

    private val rank:List<String> = generateRank()
    private val suitCharacters =listOf('\u2660','\u2665','\u2666','\u2663')
    val cardDeckReference: List<String> = generateSuit().shuffled()
    var cardDeckBeingUsed: MutableList<String>  = generateSuit().toMutableList()

    private fun generateSuit():List<String>{
        val cards:MutableList<String> = mutableListOf()

        for(i in suitCharacters){
            rank.forEach { s->
                   cards.add("$s$i")
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
