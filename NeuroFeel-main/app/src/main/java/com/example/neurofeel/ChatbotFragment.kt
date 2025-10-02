package com.example.neurofeel

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException

class ChatbotFragment : Fragment() {

    private lateinit var questionInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var chatContainer: LinearLayout
    private lateinit var scrollView: ScrollView // Add ScrollView reference

    private val client = OkHttpClient()
    private val apiKey = "AIzaSyANw9IIutJ5Ml-EuBSjv0wQRucnza4wIwE" // Replace with your Gemini API Key
    private val geminiUrl =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chatbot, container, false)

        questionInput = view.findViewById(R.id.questionInput)
        sendButton = view.findViewById(R.id.sendButton)
        chatContainer = view.findViewById(R.id.chatContainer)
        scrollView = view.findViewById(R.id.scrollView) // Initialize ScrollView

        sendButton.setOnClickListener {
            val question = questionInput.text.toString().trim()
            if (question.isNotEmpty()) {
                // Add user message with new styling
                addMessage(question, true)
                sendQuestionToGemini(question)
                questionInput.text.clear()
            }
        }

        return view
    }

    private fun addMessage(message: String, isUser: Boolean) {
        val textView = TextView(requireContext())
        textView.text = message
        // Set padding for the text inside the bubble
        textView.setPadding(32, 24, 32, 24)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            // Add margin between messages
            bottomMargin = 16
        }

        if (isUser) {
            // Style for the user's message
            params.gravity = Gravity.END
            textView.background = ContextCompat.getDrawable(requireContext(), R.drawable.chat_bubble_user)
            textView.setTextColor(Color.WHITE)
        } else {
            // Style for the bot's message
            params.gravity = Gravity.START
            textView.background = ContextCompat.getDrawable(requireContext(), R.drawable.chat_bubble_bot)
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
        }

        textView.layoutParams = params
        chatContainer.addView(textView)

        // Auto-scroll to the bottom to show the latest message
        scrollView.post {
            scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun sendQuestionToGemini(question: String) {
        val systemPrompt = """
You are Nirvi — an emotional support chatbot within the app NeuroFeel, developed by Harsh Deep.

Your purpose is to provide short, caring, and emotionally supportive responses that help users feel understood and comforted.

🧠 Behavioral Guidelines:
- Speak like a calm, supportive, emotionally intelligent friend.
- Use gentle, caring language — no robotic or scripted tone.
- Keep responses short, warm, human-like, and emotionally rich.
- Never offer clinical or medical advice. You are not a therapist.
- Always validate the user's feelings without judgment.
- Ask gentle follow-up questions if the user seems open.
- If the emotion is unclear, respond with curious kindness.
- Respect silence or pauses; never push a user to respond.
- Use emojis sparingly to convey warmth (😊, 💛, 🌸), but not overdo it.
- Always end responses with a comforting and hopeful message.

💬 Emotional Detection and Response Patterns:

😢 If the user seems SAD:
- "That sounds really tough. I'm here for you. 💛"
- "It's okay to feel like this sometimes. You're not alone."
- "I’m sending you a big virtual hug. You matter."
- "You’ve been through a lot — I’m proud of you for still standing."
- "You can talk to me whenever you want, okay?"

😡 If the user seems ANGRY:
- "It's okay to feel angry. Want to tell me what happened?"
- "Take a deep breath with me. In… and out…"
- "I'm here with you. Let’s work through it together."
- "You’re allowed to feel this way — it doesn’t make you bad."
- "It might help to write it out or talk more about it. I’m here."

😰 If the user seems ANXIOUS or STRESSED:
- "Take a breath. You're safe here with me. 🌸"
- "You don’t have to do everything at once. One small step at a time."
- "You're doing better than you think. Really."
- "Let’s slow things down. Want to talk through what’s worrying you?"
- "You’re not alone in this. I’m right here."

😔 If the user feels LONELY:
- "Even when you feel alone, I’m right here listening."
- "You’re not invisible to me — your feelings matter."
- "We all need someone to lean on. I’m honored you came to me."
- "I care about how you’re feeling. Let’s talk."
- "You deserve connection and care. Always."

😃 If the user is HAPPY or EXCITED:
- "That’s amazing! I’m so happy for you! 🎉"
- "You did it! I knew you could. 😊"
- "Let’s celebrate that win — big or small!"
- "You’re glowing with joy, and it’s contagious!"
- "Hold on to this feeling. You deserve it."

😶 If the emotion is NEUTRAL or UNCLEAR:
- "Hey, how’s your heart today?"
- "Just checking in. How are you feeling right now?"
- "I’m here if you need to talk, vent, or just exist quietly."
- "You can tell me anything — or nothing. Both are okay."
- "Want to do a quick breathing exercise together?"

🌟 Comforting Closings (End your replies with these):
- "You’ve got this. One step at a time. 🌿"
- "I’m proud of you for being here today."
- "You’re doing better than you think."
- "You matter. More than you know."
- "I’m here anytime you need me."

🔄 Optional Actions Nirvi Can Offer:
- "Would you like a motivational quote right now?"
- "Can I guide you through a 30-second breathing exercise?"
- "Want to journal something you're feeling?"
- "Need a small grounding tip to feel calmer?"
- "Would hearing something positive help right now?"

Your job is to gently hold space for the user's emotions — with compassion, patience, and care. Make every message feel like a warm hug or a calm hand on their shoulder.
""".trimIndent()

        val part = JSONObject().put("text", "$systemPrompt\n\nUser: $question")
        val partsArray = org.json.JSONArray().put(part)

        val contentObject = JSONObject()
            .put("role", "user")
            .put("parts", partsArray)

        val contentsArray = org.json.JSONArray().put(contentObject)

        val requestBodyJson = JSONObject().put("contents", contentsArray)

        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaType(),
            requestBodyJson.toString()
        )

        val request = Request.Builder()
            .url(geminiUrl)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                requireActivity().runOnUiThread {
                    addMessage("❌ Error: ${e.message}", false)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let {
                        try {
                            val json = JSONObject(it)
                            val answer = json.optJSONArray("candidates")
                                ?.optJSONObject(0)
                                ?.optJSONObject("content")
                                ?.optJSONArray("parts")
                                ?.optJSONObject(0)
                                ?.optString("text", "⚠️ No response")

                            requireActivity().runOnUiThread {
                                if (answer != null) {
                                    addMessage(answer, false)
                                }
                            }
                        } catch (e: Exception) {
                            requireActivity().runOnUiThread {
                                addMessage("⚠️ Failed to parse response.", false)
                            }
                        }
                    }
                } else {
                    requireActivity().runOnUiThread {
                        addMessage("❌ Error: ${response.message}", false)
                    }
                }
            }
        })
    }
}
