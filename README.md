# Swing-Trading-Simulator

The idea of this small project is to simulate an active swing-trading strategy according to some technical indicators and see if it outperforms a passive buy-and-hold strategy using historical stock data from a RESTful API.

How it works:

Given any valid ticker symbol, the program will fetch the historical price for that stock (this includes MACD and RSI data) and trade the stock since inception(this API seems to have data starting ~1998) according to the following criteria:


Run 1 (RSI strategy):

Buy when an oversold level (30) is reached and sell when overbought level (70) is reached.
<img src="screenshots/RSI.png">

Run 2 (MACD strategy):

Buy when the MACD line crosses above the Signal line and sell when the opposite happens.
<img src="screenshots/MACD.png">


Results for ticker symbol SPY:

(Spoiler alert: buying and holding destroy these "strategies")

https://github.com/nicoqueijo/Swing-Trading-Simulator/blob/master/output/RSI.txt

https://github.com/nicoqueijo/Swing-Trading-Simulator/blob/master/output/MACD.txt

