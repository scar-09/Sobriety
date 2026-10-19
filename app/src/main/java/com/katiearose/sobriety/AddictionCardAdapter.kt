package com.katiearose.sobriety

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.katiearose.sobriety.activities.Main
import com.katiearose.sobriety.databinding.CardAddictionBinding
import com.katiearose.sobriety.shared.Addiction
import com.katiearose.sobriety.utils.buildHeroA11y
import com.katiearose.sobriety.utils.buildHeroMainText
import com.katiearose.sobriety.utils.buildHeroTopText
import com.katiearose.sobriety.utils.convertRangeToString
import com.katiearose.sobriety.utils.convertSecondsToString
import com.katiearose.sobriety.utils.decomposeElapsed
import com.katiearose.sobriety.utils.getDateFormatPattern
import com.katiearose.sobriety.utils.getAverageAttemptsWindow
import com.katiearose.sobriety.utils.getSharedPref
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val AVERAGE_FADE_MILLIS = 200L
private const val AVERAGE_AUTO_HIDE_MILLIS = 3000L
private const val DISABLED_INFO_ALPHA = 0.4f
private const val HERO_TEXT_MIN_SP = 14
private const val HERO_TEXT_MAX_SP = 27
private const val HERO_TEXT_STEP_SP = 1

class AddictionCardAdapter(
    private val context: Context,
    private val relapseButtonAction: (Addiction) -> Unit,
    private val stopButtonAction: (Addiction) -> Unit,
    private val timelineButtonAction: (Addiction) -> Unit,
    private val priorityTextViewAction: (Addiction) -> Unit,
    private val miscButtonAction: (Addiction) -> Unit,
    private val cardButtonAction: (Addiction) -> Unit
) :
    RecyclerView.Adapter<AddictionCardAdapter.AddictionCardViewHolder>() {

    companion object {
        // Payload used by the single centralized ticker in Main to re-render elapsed time only
        const val TICK = 1
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddictionCardViewHolder {
        val binding =
            CardAddictionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddictionCardViewHolder(binding,
            { relapseButtonAction(Main.addictions[it]) },
            { stopButtonAction(Main.addictions[it]) },
            { timelineButtonAction(Main.addictions[it]) },
            { priorityTextViewAction(Main.addictions[it]) },
            { miscButtonAction(Main.addictions[it]) },
            { cardButtonAction(Main.addictions[it]) },
            { Main.addictions[it] })
    }

    override fun onBindViewHolder(
        holder: AddictionCardViewHolder,
        position: Int
    ) {
        val stringBuilder = StringBuilder(context.getString(R.string.priority)).append(": ")
        val addiction = Main.addictions[position]

        holder.textViewName.text = addiction.name
        holder.textViewPriority.text = when (addiction.priority) {
            Addiction.Priority.HIGH -> stringBuilder.append(context.getString(R.string.high))
            Addiction.Priority.MEDIUM -> stringBuilder.append(context.getString(R.string.medium))
            Addiction.Priority.LOW -> stringBuilder.append(context.getString(R.string.low))
        }
        // High-contrast container/on-container colours so the priority label stays legible
        // on any card background (pink default theme AND Material You dynamic colours).
        val (priorityBackground, priorityForeground) = when (addiction.priority) {
            Addiction.Priority.HIGH -> R.color.priority_high_bg to R.color.priority_high_fg
            Addiction.Priority.MEDIUM -> R.color.priority_medium_bg to R.color.priority_medium_fg
            Addiction.Priority.LOW -> R.color.priority_low_bg to R.color.priority_low_fg
        }
        holder.textViewPriority.chipBackgroundColor = ColorStateList.valueOf(
            ResourcesCompat.getColor(context.resources, priorityBackground, context.theme)
        )
        holder.textViewPriority.setTextColor(
            ResourcesCompat.getColor(context.resources, priorityForeground, context.theme)
        )
        val numAttempts = context.getSharedPref().getAverageAttemptsWindow()
        val averageRelapseDuration = addiction.calculateRecentAverage(numAttempts)
        holder.bindAverage(averageRelapseDuration)

        holder.refresh()
    }

    override fun onBindViewHolder(
        holder: AddictionCardViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        // The centralized ticker sends TICK; rebind only the elapsed-time text.
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else if (payloads.contains(TICK)) {
            holder.refresh()
        }
    }

    override fun getItemCount() = Main.addictions.size

    override fun onViewRecycled(holder: AddictionCardViewHolder) {
        holder.resetAverage()
        super.onViewRecycled(holder)
    }

    class AddictionCardViewHolder(
        private val binding: CardAddictionBinding,
        relapseButtonAction: (Int) -> Unit,
        stopButtonAction: (Int) -> Unit,
        timelineButtonAction: (Int) -> Unit,
        priorityTextViewAction: (Int) -> Unit,
        miscButtonAction: (Int) -> Unit,
        cardButtonAction: (Int) -> Unit,
        private val addictionSupplier: (Int) -> Addiction) : RecyclerView.ViewHolder(binding.root) {
        val textViewName: TextView = binding.textViewAddictionName
        val textViewPriority: Chip = binding.textViewPriority
        val textViewAverage: TextView = binding.textViewAverage
        private lateinit var addiction: Addiction
        private var lastAppliedStatus: Addiction.Status? = null

        fun refresh() {
            // A detached/recycled holder has no valid position; skip to avoid an index crash.
            if (adapterPosition == RecyclerView.NO_POSITION) return
            addiction = addictionSupplier(adapterPosition)
            displayInfo()
        }

        private fun getFormattedDate(milli: Long): String {
            val dateFormat = DateTimeFormatter.ofPattern(binding.root.context.getSharedPref().getDateFormatPattern())
            val date = Instant.ofEpochMilli(milli).atZone(ZoneId.systemDefault())
            return dateFormat.format(date)
        }

        private fun displayInfo() {
            val context = binding.root.context
            val now = Instant.now().toEpochMilli()
            when (addiction.status) {
                Addiction.Status.Ongoing -> {
                    val elapsed = decomposeElapsed(addiction.history.keys.last(), now)
                    val topText = buildHeroTopText(elapsed)
                    binding.textViewTop.text = topText
                    binding.textViewTop.visibility = if (topText == null) View.GONE else View.VISIBLE
                    binding.textViewTime.text = buildHeroMainText(elapsed)
                    binding.textViewTime.contentDescription = buildHeroA11y(context, elapsed)
                }

                Addiction.Status.Stopped -> {
                    binding.textViewTop.visibility = View.GONE
                    binding.textViewTime.text = context.getString(
                        R.string.stop_notice,
                        getFormattedDate(addiction.history.values.last()),
                        context.convertRangeToString(addiction.history.keys.last(), addiction.history.values.last())
                    )
                    binding.textViewTime.contentDescription = null
                }

                Addiction.Status.Future -> {
                    binding.textViewTop.visibility = View.GONE
                    binding.textViewTime.text = context.getString(
                        R.string.time_until_tracked,
                        context.convertRangeToString(now, addiction.history.keys.last())
                    )
                    binding.textViewTime.contentDescription = null
                }
            }
            // Only the live, ongoing streak is the "hero" metric. Stopped/Future use long
            // sentence strings, so they stay at the normal size to avoid awkward wrapping.
            if (lastAppliedStatus != addiction.status) {
                applyStatusStyle(context, addiction.status == Addiction.Status.Ongoing)
                lastAppliedStatus = addiction.status
            }
        }

        private fun applyStatusStyle(context: Context, isHero: Boolean) {
            if (isHero) {
                binding.textViewTime.maxLines = 1
                // Autosize derives its height budget from the wrap_content height measured at the
                // current text size, so start at the max size or it can never grow above the
                // previous (small) size.
                binding.textViewTime.setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    HERO_TEXT_MAX_SP.toFloat()
                )
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                    binding.textViewTime,
                    HERO_TEXT_MIN_SP,
                    HERO_TEXT_MAX_SP,
                    HERO_TEXT_STEP_SP,
                    TypedValue.COMPLEX_UNIT_SP
                )
                binding.textViewTime.setTypeface(null, Typeface.BOLD)
            } else {
                TextViewCompat.setAutoSizeTextTypeWithDefaults(
                    binding.textViewTime,
                    TextViewCompat.AUTO_SIZE_TEXT_TYPE_NONE
                )
                binding.textViewTime.maxLines = Int.MAX_VALUE
                binding.textViewTime.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    context.resources.getDimension(R.dimen.text_size_small)
                )
                binding.textViewTime.setTypeface(null, Typeface.NORMAL)
            }
        }

        private var averageShown = false
        private val hideAverageRunnable = Runnable { hideAverage() }

        fun bindAverage(averageDurationMillis: Long?) {
            resetAverage()
            if (averageDurationMillis == null) {
                binding.imageInfo.isEnabled = false
                binding.imageInfo.alpha = DISABLED_INFO_ALPHA
            } else {
                binding.imageInfo.isEnabled = true
                binding.imageInfo.alpha = 1f
                binding.textViewAverage.text = binding.root.context.getString(
                    R.string.recent_avg,
                    binding.root.context.convertSecondsToString(averageDurationMillis / 1000L)
                )
            }
        }

        fun resetAverage() {
            averageShown = false
            binding.textViewAverage.animate().cancel()
            binding.textViewAverage.removeCallbacks(hideAverageRunnable)
            binding.textViewAverage.alpha = 1f
            binding.textViewAverage.visibility = View.GONE
        }

        private fun toggleAverage() {
            if (!binding.imageInfo.isEnabled) return
            if (averageShown) hideAverage() else showAverage()
        }

        private fun showAverage() {
            averageShown = true
            val view = binding.textViewAverage
            view.animate().cancel()
            view.removeCallbacks(hideAverageRunnable)
            view.alpha = 0f
            view.visibility = View.VISIBLE
            view.animate().alpha(1f).setDuration(AVERAGE_FADE_MILLIS).start()
            view.postDelayed(hideAverageRunnable, AVERAGE_AUTO_HIDE_MILLIS)
        }

        private fun hideAverage() {
            averageShown = false
            val view = binding.textViewAverage
            view.animate().cancel()
            view.removeCallbacks(hideAverageRunnable)
            view.animate().alpha(0f).setDuration(AVERAGE_FADE_MILLIS).withEndAction {
                if (!averageShown) view.visibility = View.GONE
            }.start()
        }

        init {
            binding.imageInfo.setOnClickListener { toggleAverage() }
            binding.imageReset.setOnClickListener { relapseButtonAction(adapterPosition) }
            binding.imageStop.setOnClickListener { stopButtonAction(adapterPosition) }
            binding.imageTimeline.setOnClickListener { timelineButtonAction(adapterPosition) }
            textViewPriority.setOnClickListener { priorityTextViewAction(adapterPosition) }
            binding.imageMisc.setOnClickListener { miscButtonAction(adapterPosition) }
            binding.cardViewAddiction.setOnClickListener { cardButtonAction(adapterPosition) }
        }
    }
}
