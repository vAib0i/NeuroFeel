package com.example.neurofeel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FactsFragment : Fragment() {

    private lateinit var factsRecyclerView: RecyclerView

    // --- Data class for a single fact ---
    // Moved inside the fragment class
    data class Fact(val title: String, val description: String)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_facts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Find the RecyclerView in your layout
        factsRecyclerView = view.findViewById(R.id.facts_recycler_view)

        // 2. Create your list of facts
        val factsList = createDummyFacts()

        // 3. Set the LayoutManager and Adapter for the RecyclerView
        factsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        factsRecyclerView.adapter = FactsAdapter(factsList)
    }

    // Helper function to create a sample list of facts
    private fun createDummyFacts(): List<Fact> {
        return listOf(
            Fact("Your brain named itself", "The brain is the only organ that named itself... which is either brilliant or suspiciously self-centered."),
            Fact("Brains can imagine smells", "Just thinking about pizza can make your brain 'smell' it — no delivery required."),
            Fact("Yawning is brain air-conditioning", "Your brain makes you yawn to cool itself down, like it's got a built-in fan."),
            Fact("You forget on purpose", "Your brain deletes stuff so you don’t walk around remembering your neighbor’s weird socks forever."),
            Fact("Brain runs on electricity", "At rest, your brain produces enough electricity to power a lightbulb. Edison would be proud."),
            Fact("Talking to yourself is genius behavior", "When you talk to yourself, your brain thinks you're smart. Be your own TED Talk!"),
            Fact("Your brain has fake memories", "It makes up things you never did. So that time you ‘definitely replied’? Hmm..."),
            Fact("You can’t tickle yourself", "Your brain knows it’s coming, so the surprise is ruined. Sad giggle."),
            Fact("Brains love shiny things", "You’re not distracted, you’re just evolutionarily optimized. Like a curious magpie."),
            Fact("Brain slows down at 24", "It starts caring less what people think. Basically, your brain hits its cool phase.")
        )
    }

    // --- RecyclerView Adapter as an Inner Class ---
    inner class FactsAdapter(private val factsList: List<Fact>) : RecyclerView.Adapter<FactsAdapter.FactViewHolder>() {

        // This ViewHolder class is also an inner class
        inner class FactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleTextView: TextView = itemView.findViewById(R.id.fact_title)
            val descriptionTextView: TextView = itemView.findViewById(R.id.fact_description)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_fact_card, parent, false)
            return FactViewHolder(view)
        }

        override fun onBindViewHolder(holder: FactViewHolder, position: Int) {
            val currentFact = factsList[position]
            holder.titleTextView.text = currentFact.title
            holder.descriptionTextView.text = currentFact.description
        }

        override fun getItemCount(): Int {
            return factsList.size
        }
    }
}

