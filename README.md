# coap-server-dht

Projeto que atende os seguintes requisitos:

Projetar um sistema cliente servidor IoT que seja capaz de controlar de forma remota o acendimento de um conjunto de Leds de acordo com a temperatura e umidade corrente do ambiente. 
Um dos leds deve ser aceso sempre que a temperatura lida estiver acima um valor definido pelo usuário. 
O outro led deve ser aceso de forma similar mas considerando o valor corrente da umidade. 
Caso tanto a temperatura quanto a umidade estiverem acima/abaixo dos valores limites os leds deve começar a piscar. 
O estado dos leds também deve ser informado na interface da aplicação cliente. 
A comunicação entre o programa cliente e o programa servidor deve ser feita usando o protocolo COAP. 
O programa servidor deve ser capaz de atender a diversos programas cliente simultaneamente. 
