package ru.kremlev.ncsrecognitonmanager.manager.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate

import java.text.SimpleDateFormat
import java.util.*

import ru.kremlev.ncsrecognitonmanager.databinding.FragmentStatisticBinding
import ru.kremlev.ncsrecognitonmanager.manager.data.RecognitionSystemData
import ru.kremlev.ncsrecognitonmanager.manager.viewmodels.RecognitionSystemViewModel
import ru.kremlev.ncsrecognitonmanager.utils.LogManager

class StatisticFragment : Fragment(), OnChartValueSelectedListener {
    private var _binding: FragmentStatisticBinding? = null
    private val binding: FragmentStatisticBinding
        get() = _binding!!

    private val model: RecognitionSystemViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        val view = binding.root

        setupGraph()

        model.getSelectedSystem().observe(viewLifecycleOwner) { it ->
            LogManager.d()
            binding.tvId.text =
                if (it > -1) {
                    (updateGraph(it)?.id ?: "No selected").toString()
                } else
                    "Please Select System At Manager Page"
        }

        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val format: SimpleDateFormat = SimpleDateFormat("dd/MMM/yyyy hh:mm:ss", Locale.ENGLISH)
        binding.tvSelectedItem.text = "Chosen Point: (${e?.y} : ${format.format(Date(e?.x?.toLong() ?: 0L))})"
    }

    @SuppressLint("SetTextI18n")
    override fun onNothingSelected() {
        binding.tvSelectedItem.text = "Please Select Point"
    }

    private fun setupGraph() {
        binding.graph.xAxis.position = XAxis.XAxisPosition.BOTTOM

        binding.graph.setOnChartValueSelectedListener(this)

        val xAxis: XAxis = binding.graph.xAxis
        xAxis.textColor = Color.WHITE
        xAxis.granularity = .25f
        xAxis.valueFormatter = object : ValueFormatter() {
            private val mFormat: SimpleDateFormat = SimpleDateFormat("mm:ss", Locale.ENGLISH)
            override fun getFormattedValue(value: Float): String {
                return mFormat.format(Date(value.toLong()))
            }
        }

        val yAxis: YAxis = binding.graph.axisLeft
        yAxis.textColor = Color.WHITE

        val rightAxis: YAxis = binding.graph.axisRight
        rightAxis.isEnabled = false

        val description = Description()
        description.isEnabled = false
        binding.graph.description = description

        // enable touch gestures
        binding.graph.setTouchEnabled(true)

        // enable scaling and dragging
        binding.graph.isDragEnabled = true
        binding.graph.setScaleEnabled(true)
    }

    private val colors = intArrayOf(
        ColorTemplate.VORDIPLOM_COLORS[0],
        ColorTemplate.VORDIPLOM_COLORS[1],
        ColorTemplate.VORDIPLOM_COLORS[2]
    )

    private fun updateGraph(index: Int): RecognitionSystemData? {
        val currentSystem = model.systemList.value?.get(index)

        val dataSets = ArrayList<ILineDataSet>()

        if (currentSystem?.personData?.isNotEmpty() == true) {
            currentSystem.personData.forEachIndexed { personInd, personData ->
                val values = ArrayList<Entry>()

                personData.probs.forEachIndexed { ind, prob ->
                    values.add(Entry(personData.timestamps[ind].ms.toFloat(), prob))
                }

                val lineDataSet = LineDataSet(values, personData.personID)

                lineDataSet.color = colors[personInd % colors.size]
                lineDataSet.valueTextColor = colors[personInd % colors.size]
                lineDataSet.setCircleColor(colors[personInd % colors.size])
                dataSets.add(lineDataSet)
            }

            val data = LineData(dataSets)
            binding.graph.data = data
            binding.graph.invalidate()
        }
        return currentSystem
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}