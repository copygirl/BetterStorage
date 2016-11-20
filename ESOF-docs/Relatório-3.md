# Relatório 3



## Índice


  1. [Introdução](#introduction)
  2. [Vista Lógica](#vista lógica)
  3. [Vista de Implementação](#vista implementacao)
  4. [Vista de Distribuição](#vista distribuicao)
  5. [Vista de Processo](#vista processo)
  6. [Conclusões e Análise Crítica](#analise)
  7. [Links Externos](#links)
  8. [Contribuições](#contribuicoes)
  9. [Identificação do Grupo](#id)

<a name = "introduction" >
## Introdução


  Neste relatório serão abordados alguns aspetos referentes à arquitetura do **BetterStorage**, utilizando o [modelo de vistas **4+1**](https://es.wikipedia.org/wiki/Modelo_de_Vistas_de_Arquitectura_4%2B1), permitindo analisar a arquitetura usada neste projeto.
  Para o efeito, foram elaborados **cinco diagramas UML** representando cada uma das 5 vistas relativas ao modelo respetivo.

  Esses diagramas consistem em:
   - **Diagrama de packages**, referente à **vista lógica**;
   - **Diagrama de componentes**, referente à **vista de implementação**;
   - **Diagrama de distribuição**, referente à **vista de distribuição**;
   - **Diagrama de atividades**, referente à **vista de processo**;
   - **Diagrama de casos de uso**, referente à **vista de casos de uso**, já elaborado no [relatório anterior](https://github.com/VascoUP/BetterStorage/blob/master/ESOF-docs/Relat%C3%B3rio-2.md).


<p align="center">
  <img src="https://github.com/VascoUP/BetterStorage/blob/master/ESOF-docs/resources/4+1.gif">
</p>


  Depois de uma análise ao código do **BetterStorage**, a toda a sua arquitetura e forma como o mesmo está organizado, o grupo verificou que não existe uma arquitetua pré-estabelicida ou pré-definida. No entanto, todo o seu código está dividido em diferentes *packages*, de forma a organizar o código implementado e de forma a que todos os contribuidores consigam perceber qual a finalidade daquele *package* e por conseguinte, qual a finalidade do código nele presente.

<a name = "vista lógica" >
## Vista Lógica


   De acordo com o que nos foi possível apurar depois de uma análise ao código, a arquitetura do projeto foi concebida da seguinte forma:


<p align="center">
  <img src="https://github.com/VascoUP/BetterStorage/blob/master/ESOF-docs/resources/Package%20Diagram.png">
</p>


  O *package* "***Better Storage***” representa o início do jogo, isto é, onde ocorre a inicialização do programa. Este usa, então, vários *packages* para inicializar várias instâncias necessárias ao desenvolvimento do jogo, inicializando o *package* “***Network***”, responsável pela parte de rede do jogo, o *package* “***Config***”, responsável pelas configurações base do programa, o *package* “***Content***”, que regista os “***Items***”, “***Entities***” e “***Tiles***”, o package “***Addon***” e por fim o *package* “***Proxy***”.

  De salientar, a existência de um *package* “***Utils***” responsável por guardar todas as variáveis e/ou funções que são usadas em comum por outros *packages*. Deste modo, todos os *packages* importam o *package* “***Utils***”. No entanto, no diagrama UML foi retirado o *import* de todos os *packages*, ao mesmo, de forma a simplificar a leitura do diagrama, sendo que mesmo assim a inclusão do mesmo está implícito hierarquicamente.

<a name = "vista implementacao" >
## Vista de Implementação


  Um [**diagrama de componentes**](https://pt.wikipedia.org/wiki/Diagrama_de_componentes) permite saber como é que o *software* está decomposto, através da utilização de **componentes**.

  De seguida, é possível visualizar-se a análise feita à arquitetura do projeto do ponto de vista da implementação, detalhando os principais componentes do **BetterStorage** e interações entre eles.


<p align="center">
  <img src="https://github.com/VascoUP/BetterStorage/blob/master/ESOF-docs/resources/Component_Model.png">
</p>


  O componente "chave" deste projeto é o componente **BetterStorage**, que tendo o mesmo nome que o *mod* desenvolvido, indica que é aquele onde estão centralizadas todas as funções principais, uma vez que é o mesmo que permite a criação "final" do *mod*.

  O componente **Network** tem como objetivo primordial verificar se a ligação está a ser estabelecida pelo servidor ou pelo próprio cliente, permitindo a comunicação entre os mesmos.

  O componente **Proxy** trata dos registos de ocorrências por parte do utilizador/cliente, inicializando os respetivos *renderers* relativos aos itens criados ou colocados em ambiente de jogo. Este componente necessita da informação relativa ao componente **Item**, onde são criados os itens do *mod*. Para além disso, **Proxy**, permite que a **API** do *mod* seja criada.

<a name = "vista distribuicao" >
## Vista de Distribuição


  Um [**diagrama de distribuição**](https://en.wikipedia.org/wiki/Deployment_diagram) permite perceber quais os requisitos necessários para executar o projeto criado, mostrando de que forma os artefactos do sistema são distribuídos pelos nós de *hardware*. Esses artefactos manifestam fisicamente os componentes de *software* e relacionam-se com certos componentes de *hardware*.

   Neste caso, o diagrama respetivo representa a vista de distribuição relativa à criação do *mod* **BetterStorage**.


<p align="center">
  <img src="https://github.com/VascoUP/BetterStorage/blob/master/ESOF-docs/resources/Deployment_Model.png">
</p>


  Para que este *mod* seja utilizado é necessário que haja um computador cujo sistema operativo seja **Linux**, **Windows** ou **Mac OS X**. Para além disso, é necessário que o mesmo tenha instalada uma versão recente do **Java** (**JDK -** Java Development Kit ou **JRE -** Java Runtime Environment), assim como tenha instalado o [**Minecraft Forge**](https://files.minecraftforge.net/) para que seja possível a utilização do *mod*.

  Após todos estes "requisitos" serem cumpridos, já se pode utilizar o *mod* através do ficheiro **BetterStorage.jar**.

<a name = "vista processo" >
## Vista de Processo


  Um [**diagrama de actividade**](https://pt.wikipedia.org/wiki/Diagrama_de_atividade) permite saber como é o fluxo de controlo entre actividades num *software*.
  O diagrama respetivo à representação da vista de processo do *mod* **BetterStorage** é o seguinte:


<p align="center">
  <img src="https://github.com/VascoUP/BetterStorage/blob/master/ESOF-docs/resources/Activity_Model.png">
</p>


  O *mod* **BetterStorage** recorre ao [**Minecraft Forge**](https://files.minecraftforge.net/) para ser inicializado no *Minecraft*. O [**Minecraft Forge**](https://files.minecraftforge.net/) é um *loader* que permite simplificar a compatibilidade entre *mods* e *Minecraft*. Para esse efeito, recorre aos 3 eventos vistos no diagrama acima:
  * **PreInitialization**: ler configurações, criar blocos, items, entre outros, e colocando-os no *GameRegistry*;
  * **Load**: fazer *setup* do *mod*, adicionar receitas ao registo;
  * **PostInitialization**: gerir interações com outros *mods*.


<a name = "analise" >
## Conclusões e Análise Crítica


  Antes de serem expostas todas as conclusões e análises relativas à arquitetura do **BetterStorage**, é importante referir que todos os diagramas criados e apresentados neste relatório são da autoria dos respetivos elementos do grupo de trabalho, não tendo havido qualquer ajuda por parte dos contribuidores principais do projeto, uma vez que não se conseguiu estabelecer contacto para saber mais informações referentes ao **BetterStorage** e à sua arquitetura.
  
  Dito isto, o grupo considerou que apesar de não haver uma arquitetura pré-estabelecida e bem definida, o **BetterStorage** está bem organizado, sendo dividido em diversos *packages* que contêm o código referente a essa parte do projeto. Contudo, apesar dos contribuidores terem uma ideia do que se passa em cada *package*, achámos que a análise relativa à arquitetura do projeto seria mais simples se os contribuidores comentassem o seu código ou criassem um documento explicativo acerca de cada pacote (*package*). 
  
  A falta de informação relativa à estruturação do **BetterStorage** dificultou um pouco a análise executada pelo grupo, no entanto não impediu que fossem executados os diagramas referentes ao [modelo de vistas **4+1**](https://es.wikipedia.org/wiki/Modelo_de_Vistas_de_Arquitectura_4%2B1). O único diagrama que pode ter um carácter subjetivo é o **diagrama de componentes** (vista de implementação), uma vez que o grupo pode ter uma interpretação relativa aos componentes do **BetterStorage**, que difere em relação à opinião e entendimento de outras pessoas.
  
  

<a name = "links" >
## Links Externos

   - Fórum do projeto: http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/wip-mods/1442380-betterstorage-0-13-1-127-wip
    
   - Modelo de Vistas 4+1: https://es.wikipedia.org/wiki/Modelo_de_Vistas_de_Arquitectura_4%2B1
   
   - Diagrama de componentes: https://pt.wikipedia.org/wiki/Diagrama_de_componentes

   - Diagrama de distribuição: https://en.wikipedia.org/wiki/Deployment_diagram

   - Diagrama de actividade: https://pt.wikipedia.org/wiki/Diagrama_de_atividade
   
   - Como funciona o **Minecraft Forge**: http://greyminecraftcoder.blogspot.pt/2013/11/how-forge-starts-up-your-code.html

<a name = "contribuicoes" >
## Contribuições

  - Bruno Santos: **25%**
  - Sara Fernandes: **25%**
  - Vasco Pereira: **25%**
  - Vasco Ribeiro: **25%**

<a name = "id" >
## Identificação do Grupo

####Grupo 05, Turma 03:

   - Bruno Santos (up201402962@fe.up.pt)
   - Sara Fernandes (up201405955@fe.up.pt)
   - Vasco Pereira (up201403485@fe.up.pt)
   - Vasco Ribeiro (up201402723@fe.up.pt)
