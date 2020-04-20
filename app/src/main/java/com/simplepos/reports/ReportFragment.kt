package com.simplepos.reports

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.simplepos.R
import com.simplepos.about.AboutActivity
import com.simplepos.base.BaseFragment
import com.simplepos.util.toDefaultCurrencyFormat
import com.simplepos.util.toFormatString
import kotlinx.android.synthetic.main.fragment_report.view.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class ReportFragment : BaseFragment(), ReportAdapter.OnClickListener,AdapterView.OnItemSelectedListener {

    private val reportViewModel:ReportViewModel by viewModel()
    private lateinit var adapter : ReportAdapter
    private lateinit var orderToolbar:androidx.appcompat.widget.Toolbar
    private var startCal = Calendar.getInstance()
    private var untilCal = Calendar.getInstance()
    private lateinit var clearAlert:AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView =  inflater.inflate(R.layout.fragment_report, container, false)
        setAllObservers()
        setAllAdapters()
        setInitialView()
        setAllListeners()

        return rootView
    }


    private fun setAllAdapters(){
        activity?.let {fragmentActivity->
            adapter = ReportAdapter(fragmentActivity)
            adapter.setClickListener(this)
            rootView.report_success_order.adapter = adapter
            rootView.report_success_order.layoutManager = LinearLayoutManager(fragmentActivity)
        }
    }

    override fun setAllObservers(){
        reportViewModel.successOrders.observe(viewLifecycleOwner, Observer {
            adapter.setItems(it)
        })
        reportViewModel.totalSell.observe(viewLifecycleOwner, Observer {
            rootView.report_text_total.text = it.toDefaultCurrencyFormat()
        })
        reportViewModel.totalMargin.observe(viewLifecycleOwner, Observer {
            rootView.report_text_margin.text = it.toDefaultCurrencyFormat()
        })
        reportViewModel.periodeType.observe(viewLifecycleOwner, Observer {
            rootView.report_spinner_period.setSelection(it)
            reportViewModel.orderDate.value?.let {orderDate->
                reportViewModel?.periodeType.value?.let {periodeType->
                    rootView.report_text_date.text = orderDate.toFormatString(reportViewModel.dateFormats[periodeType])
                }
            }

        })
        reportViewModel.orderDate.observe(viewLifecycleOwner, Observer { orderDate->
            reportViewModel?.periodeType.value?.let {periodeType->
                rootView.report_text_date.text = orderDate.toFormatString(reportViewModel.dateFormats[periodeType])
            }
        })
        reportViewModel.startOrderDate.observe(viewLifecycleOwner, Observer { orderDate->
            reportViewModel?.periodeType.value?.let {periodeType->
                rootView.report_btn_date_start.text = orderDate.toFormatString(reportViewModel.dateFormats[0])
            }
            startCal.time = orderDate
            reportViewModel.getSuccessOrders()
        })
        reportViewModel.untilOrderDate.observe(viewLifecycleOwner, Observer { orderDate->
            reportViewModel?.periodeType.value?.let {periodeType->
                rootView.report_btn_date_until.text = orderDate.toFormatString(reportViewModel.dateFormats[0])
            }
            untilCal.time = orderDate
            reportViewModel.getSuccessOrders()
        })

    }

    override fun setAllListeners(){

        activity?.let {
            val dialogBuilder= AlertDialog.Builder(it)
            dialogBuilder.setTitle("Konfirmasi")
            dialogBuilder.setMessage("Apakah anda ingin menghapus data?")

            dialogBuilder.setPositiveButton("Hapus",
                DialogInterface.OnClickListener { dialog, which ->
                    reportViewModel.clearSales()
                })

            dialogBuilder.setNegativeButton("Tidak",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })

            clearAlert = dialogBuilder.create()
        }

        rootView.report_toolbar.apply {
            orderToolbar = this
            inflateMenu(R.menu.menu_report)
            setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.report_item_clear->{
                        clearAlert.show()
                        true
                    }
                    R.id.report_item_about ->{
                        activity?.let { startActivity(Intent(it,AboutActivity::class.java)) }
                        true
                    }
                    else -> super.onOptionsItemSelected(menuItem)
                }
            }
        }
        rootView.report_spinner_period.onItemSelectedListener = this
        rootView.report_btn_next_date.setOnClickListener{
            reportViewModel.setToNextDate()
        }
        rootView.report_btn_prev_date.setOnClickListener{
            reportViewModel.setToPrevDate()
        }

        rootView.report_btn_date_start.setOnClickListener {
            openStartDatePickerDialog()
        }
        rootView.report_btn_date_until.setOnClickListener{
            openUntilDatePickerDialog()
        }

    }


    private fun openStartDatePickerDialog(){

        activity?.let {
            DatePickerDialog(it,DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                reportViewModel.setStartOrderDate(
                    GregorianCalendar(year,month,dayOfMonth).time
                )
            },startCal.get(Calendar.YEAR),startCal.get(Calendar.MONTH),startCal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun openUntilDatePickerDialog(){

        activity?.let {
            DatePickerDialog(it,DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                reportViewModel.setUntilOrderDate(
                    GregorianCalendar(year,month,dayOfMonth).time
                )
            },untilCal.get(Calendar.YEAR),untilCal.get(Calendar.MONTH),untilCal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }


    override fun onResume() {
        super.onResume()
        reportViewModel.getSuccessOrders()
    }

    override fun onClickItem(v: View, position: Int) {

    }

    override fun setInitialView(){

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        reportViewModel.setPeriodeType(position)
    }


}
