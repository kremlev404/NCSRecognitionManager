package ru.kremlev.ncsrecognitonmanager.manager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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

        model.getSelectedSystem().observe(viewLifecycleOwner) { it ->
            LogManager.d()

            binding.tvId.text =
                if (it > -1) {
                    val currentSystem = model.systemList.value?.get(it)
                    val data = arrayListOf<Entry>()
                    val lineDataSet: MutableList<ILineDataSet> = arrayListOf()
                    if(currentSystem?.personData?.isNotEmpty() == true) {
                        currentSystem.personData.forEach { personData ->
                            personData.probs.forEachIndexed { ind, prob ->
                                data.add(Entry(personData.timestamps[ind].ms.toFloat(), prob))
                            }
                            lineDataSet.add(LineDataSet(data, personData.personID))

                        }
                        val ld: LineData = LineData(lineDataSet)
                        binding.graph.setData(ld)
                        binding.graph.invalidate()
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