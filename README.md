# App conversor moedas/cambio com Kotlin.

## Este projeto é um requisito para formação no curso Carrefour Android Developer realizado na Plataforma da DIO https://web.dio.me/. 


## Projeto ministrado por Ezequiel_Messore

- De maneira geral a maior parte do projeto foi apenas refazer os passos do Ezequiel ( Com preocupação apenas de compreender as melhores práticas de desenvolvimento )
- Alterado o data class ExchangeResponseValue incrementando o valor de entrada, tipo de moeda de entrada e a data em que foi realizada a pesquisa.
  - Ex. de saída no histórico:
      - moeda entrada/moeda saida data(dd/MM/yyyy HH:mm:ss)

- Alterado no autoCompleteTextView (main activy) e no enum class Coin funcionalidades que carreguem no segundo autoCompleteTextView todos os dados do enum exceto o já selecionado no primeiro,  evitando o erro que ocorre quando usuário seleciona a mesma moeda nos dois autoCompleteTextView.
