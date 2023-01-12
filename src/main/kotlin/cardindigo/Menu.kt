package cardindigo

class Menu(private val cards: Cards){
    private fun printWrongAction(){
        println("Wrong action.")
    }
    fun execute(){
        var play = true
        while(play){
            println("Choose an action (reset, shuffle, get, exit):")
            // readlnOrNull()?: throw IllegalStateException()
            when(readLine() ?: throw IllegalStateException()){
                "reset" -> {if (cards.reset() != 0) {
                    printWrongAction()} else {
                        println("Card deck is reset.")
                    }
                }
                "shuffle" -> {if ( cards.reshuffle() != 0) printWrongAction()
                else println("Card deck is shuffled.")
                }
                "get" -> {
                    cards.get() }
                "exit" -> {
                    println("Bye")
                    play = false
                }
                else ->{
                    printWrongAction()
                }
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

fun Cards.get():Int{
    val theRange = 1..52
    val input: Int
    println("Number of cards:")
    //test if input is a number:
    try{
       input = readln().toInt()
    }
    catch (e:Exception){
        println("Invalid number of cards.")
        return -1
    }
    return when(input){
        in theRange -> {
            if(this.cardDeckBeingUsed.size < input){
                println("The remaining cards are insufficient to meet the request.")
                return -1
            }
            else{
                val indexStart = this.cardDeckBeingUsed.size - input
                val obtainedCards = mutableListOf<String>()
                //add the cards:
                for(i in indexStart until this.cardDeckBeingUsed.size){
                   obtainedCards.add(this.cardDeckBeingUsed[i])
                }
                //remove cards from deck:
                for(i in obtainedCards){
                    if(i in this.cardDeckBeingUsed) this.cardDeckBeingUsed.remove(i)
                }
                //print it out:
                obtainedCards.forEach{
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