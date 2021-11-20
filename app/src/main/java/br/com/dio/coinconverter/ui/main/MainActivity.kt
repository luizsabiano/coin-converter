package br.com.dio.coinconverter.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import br.com.dio.coinconverter.R
import br.com.dio.coinconverter.core.extensions.createDialog
import br.com.dio.coinconverter.core.extensions.createProgressDialog
import br.com.dio.coinconverter.core.extensions.formatCurrency
import br.com.dio.coinconverter.core.extensions.hideSoftKeyboard
import br.com.dio.coinconverter.core.extensions.text
import br.com.dio.coinconverter.data.model.Coin
import br.com.dio.coinconverter.databinding.ActivityMainBinding
import br.com.dio.coinconverter.presentation.MainViewModel
import br.com.dio.coinconverter.ui.history.HistoryActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.widget.AutoCompleteTextView
import java.text.DateFormat
import java.text.SimpleDateFormat

import java.util.*


class MainActivity : AppCompatActivity() {

    private val viewModel by viewModel<MainViewModel>()
    private val dialog by lazy { createProgressDialog() }
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        bindAdapters()
        bindListeners()
        bindObserve()

        setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_history) {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun bindAdapters() {
        var autoCompleteTextView: AutoCompleteTextView? = null
        val list = Coin.values()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)

        //binding.tvFrom.setAdapter(adapter)

        var listTvTo = Coin.getAllMinusThis(Coin.USD.name)
        var adapterTvTo = ArrayAdapter(this, android.R.layout.simple_list_item_1, listTvTo)
        binding.tvTo.setAdapter(adapterTvTo)
        autoCompleteTextView = binding.tvFrom
        autoCompleteTextView.setAdapter(adapter)


        binding.tvFrom.setText(Coin.USD.name, false)
        binding.tvTo.setText(Coin.BRL.name, false)


        //                   -*****-



        autoCompleteTextView.onItemClickListener  = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()

            listTvTo = Coin.getAllMinusThis(selectedItem)
            adapterTvTo = ArrayAdapter(this, android.R.layout.simple_list_item_1, listTvTo)
            binding.tvTo.setAdapter(adapterTvTo)
            if (selectedItem == "USD")
                binding.tvTo.setText(Coin.BRL.name, false)
            else
                binding.tvTo.setText(listTvTo[0].name, false)

        }







    }

    private fun bindListeners() {
        binding.tilValue.editText?.doAfterTextChanged {
            binding.btnConverter.isEnabled = it != null && it.toString().isNotEmpty()
            binding.btnSave.isEnabled = false
        }

        binding.btnConverter.setOnClickListener {
            it.hideSoftKeyboard()

            val search = "${binding.tilFrom.text}-${binding.tilTo.text}"

            viewModel.getExchangeValue(search)
        }

        binding.btnSave.setOnClickListener {
            val value = viewModel.state.value

            val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val date = Date()
            //01/02/2019 14:08:43

            (value as? MainViewModel.State.Success)?.let {
                val exchange = it.exchange.copy(bid = it.exchange.bid, bidin = binding.tilValue.text.toDouble(), date = dateFormat.format(date))
                viewModel.saveExchange(exchange)
            }
        }


    }

    private fun bindObserve() {
        viewModel.state.observe(this) {
            when (it) {
                MainViewModel.State.Loading -> dialog.show()
                is MainViewModel.State.Error -> {
                    dialog.dismiss()
                    createDialog {
                        setMessage(it.error.message)
                    }.show()
                }
                is MainViewModel.State.Success -> success(it)
                MainViewModel.State.Saved -> {
                    dialog.dismiss()
                    createDialog {
                        setMessage("item salvo com sucesso!")
                    }.show()
                }
            }
        }
    }

    private fun success(it: MainViewModel.State.Success) {
        dialog.dismiss()
        binding.btnSave.isEnabled = true

        val selectedCoin = binding.tilTo.text
        val coin = Coin.getByName(selectedCoin)

        val result = it.exchange.bid * binding.tilValue.text.toDouble()

        binding.tvResult.text = result.formatCurrency(coin.locale)
    }
}

// Alterado o data class ExchangeResponseValue incrementando o valor de entrada,
// tipo de moeda de entrada e a data em que foi realizada a pesquisa.
// Ex. de saída no histórico:
//      moeda entrada/moeda saida data(dd/MM/yyyy HH:mm:ss)

// Alterado no autoCompleteTextView (main activy) e no enum class Coin funcionalidades que carraguem no segundo
// autoCompleteTextView todos os dados do enum exceto o já selecionado no primeiro,
// evitando o erro que ocorre quando usuário seleciona a mesma moeda nos dois autoCompleteTextView