# Swing-Trading-Simulator

The idea of this small project is to simulate an active swing-trading strategy according to some technical indicators and see if it outperforms a passive buy-and-hold strategy using historical stock data from a RESTful API.

How it works:<br/>
Given any valid ticker symbol, the program will fetch the historical prices for that stock (this includes RSI and MACD data) and trade the stock since inception according to the following criterias.<br/>
(This API seems to have data starting ~1998).


Run 1 (RSI strategy):<br/>
Buy when an oversold level (30) is reached and sell when overbought level (70) is reached.
<img src="screenshots/RSI.png">

Run 2 (MACD strategy):<br/>
Buy when the MACD line crosses above the Signal line and sell when the opposite happens.
<img src="screenshots/MACD.png">


Results for ticker symbol SPY:<br/>
(Spoiler alert: buying and holding destroy these "strategies")<br/>
https://github.com/nicoqueijo/Swing-Trading-Simulator/blob/master/output/RSI.txt<br/>
https://github.com/nicoqueijo/Swing-Trading-Simulator/blob/master/output/MACD.txt

