package ru.kremlev.ncsrecognitonmanager.manager.fragments

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import ru.kremlev.ncsrecognitonmanager.databinding.FragmentStatisticBinding
import ru.kremlev.ncsrecognitonmanager.manager.viewmodels.RecognitionSystemViewModel
import ru.kremlev.ncsrecognitonmanager.utils.LogManager


class StatisticFragment : Fragment() {
    private var _binding: FragmentStatisticBinding? = null
    private val binding: FragmentStatisticBinding
        get() = _binding!!

    private val model: RecognitionSystemViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        val view = binding.root

        val graphColorList = arrayOf(
            requireContext().getColor(R.color.holo_red_dark),
            requireContext().getColor(R.color.holo_orange_dark),
            requireContext().getColor(R.color.holo_green_light),
            requireContext().getColor(R.color.holo_purple),
            requireContext().getColor(R.color.holo_orange_light),
            requireContext().getColor(R.color.black),
        )
        model.getSelectedSystem().observe(viewLifecycleOwner) { it ->
            LogManager.d()

            binding.tvId.text =
                if (it > -1) {
                    val currentSystem = model.systemList.value?.get(it)
                    val data = arrayListOf<Entry>()
                    val lineDataSet: ArrayList<ILineDataSet> = arrayListOf()
                    if (currentSystem?.personData?.isNotEmpty() == true) {
                        currentSystem.personData.forEachIndexed { presonInd, personData ->
                            personData.probs.forEachIndexed { ind, prob ->
                                data.add(Entry(personData.timestamps[ind].ms.toFloat(), prob))
                            }
                            val iLine = LineDataSet(data, personData.personID)
                            iLine.setColor(graphColorList[presonInd])
                            iLine.setCircleColor(graphColorList[presonInd])
                            lineDataSet.add(iLine)
                        }

                        val ld: LineData = LineData(lineDataSet)
                        binding.graph.data = ld

                        binding.graph.xAxis.position = XAxis.XAxisPosition.BOTTOM
                        val description = Description()
                        description.isEnabled = false
                        binding.graph.description = description
                        binding.graph.notifyDataSetChanged()

                    }
                    (currentSystem?.id ?: "No selected").toString()
                } else
                    "Please Select System At Manager Page"
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}