package ru.kremlev.ncsrecognitonmanager.manager.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asFlow

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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

import ru.kremlev.ncsrecognitonmanager.databinding.FragmentStatisticBinding
import ru.kremlev.ncsrecognitonmanager.manager.data.RecognitionSystemData
import ru.kremlev.ncsrecognitonmanager.manager.model.NCSFirebase
import ru.kremlev.ncsrecognitonmanager.manager.viewmodels.RecognitionSystemViewModel
import ru.kremlev.ncsrecognitonmanager.utils.LogManager

class StatisticFragment : Fragment(), OnChartValueSelectedListener {
    private var _binding: FragmentStatisticBinding? = null
    private val binding: FragmentStatisticBinding
        get() = _binding!!

    private val mutex = Mutex()

    private val model: RecognitionSystemViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        val view = binding.root

        setupGraph()

        CoroutineScope(Dispatchers.Default).launch {
            model.getSelectedSystem().asFlow().collect { it ->
                LogManager.d()
                val selectedSystem = if (it > -1) {
                    (updateGraph(it)?.id ?: "No selected").toString()
                } else
                    "Please Select System At Manager Page"
                withContext(Dispatchers.Main) {
                    binding.tvId.text = selectedSystem
                }
            }
        }
        model.systemList.observe(viewLifecycleOwner) {
            CoroutineScope(Dispatchers.Default).launch {
                model.getSelectedSystem().asFlow().collect { selected ->
                    if (selected > -1)
                        updateGraph(selected)
                }
            }
        }

        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val format: SimpleDateFormat = SimpleDateFormat("dd/MMM/yyyy hh:mm:ss:ms", Locale.ENGLISH)
        binding.tvSelectedItem.text =
            "Chosen Point: (${e?.y} : ${format.format(Date(e?.x?.toLong() ?: 0L))})"
        val tsLong = e?.x?.toLong()

        binding.tvDeleteItem.visibility = View.VISIBLE

        binding.tvDeleteItem.setOnClickListener {
            val dataSetIndex = h?.dataSetIndex ?: return@setOnClickListener
            val pId = binding.graph.data.getDataSetByIndex(dataSetIndex).label
            val index = binding.graph.data.getDataSetByIndex(dataSetIndex).getEntryIndex(e)

            val rsID = model.getSelectedSystem().value?.let { selected ->
                val systemList = model.systemList.value
                if (selected < systemList?.size!!)
                    model.systemList.value?.get(selected)?.id
                else
                    null
            }

            if (rsID != null && pId != null && tsLong != null && index > -1) {
                NCSFirebase.deletePoint(
                    "mId:${model.currentUser.value.toString()}",
                    "rsId:${rsID}",
                    "pId:${pId}",
                    index
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onNothingSelected() {
        binding.tvSelectedItem.text = "Please Select Point"
        binding.tvDeleteItem.visibility = View.GONE
    }

    private fun setupGraph() {
        binding.graph.xAxis.position = XAxis.XAxisPosition.BOTTOM

        binding.graph.setOnChartValueSelectedListener(this)

        val xAxis: XAxis = binding.graph.xAxis
        xAxis.textColor = Color.WHITE
        xAxis.granularity = .25f
        xAxis.valueFormatter = object : ValueFormatter() {
            private val mFormat: SimpleDateFormat = SimpleDateFormat("hh:mm:ss", Locale.ENGLISH)
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
        ColorTemplate.VORDIPLOM_COLORS[2],
        ColorTemplate.VORDIPLOM_COLORS[3],
        ColorTemplate.VORDIPLOM_COLORS[4],
        ColorTemplate.COLORFUL_COLORS[0],
        ColorTemplate.COLORFUL_COLORS[1],
        ColorTemplate.COLORFUL_COLORS[2],
        ColorTemplate.COLORFUL_COLORS[3],
        ColorTemplate.COLORFUL_COLORS[4]
    )

    private suspend fun updateGraph(index: Int): RecognitionSystemData? {
        //mutex.withLock {
        var currentSystem: RecognitionSystemData? = null

        try {
            currentSystem = model.systemList.value?.get(index)
        } catch (e: Exception) {
            e.printStackTrace()
            return currentSystem
        }

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
            withContext(Dispatchers.Main) {
                binding.graph.data = data
                binding.graph.invalidate()
            }
        }
        return currentSystem
        //}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}