
#**Relatório 2**



##Breve descrição do projeto e respetivas funcionalidades


**BetterStorage** é um *mod* para o jogo [Minecraft](https://minecraft.net/pt/), desenvolvido em **Java**, e que tem como objectivo aumentar e diversificar as opções de armazenamento de recursos no jogo.

Após serem recolhidos os respetivos recursos necessários para a construção das opções de armazenamento, o jogador pode colocar as mesmas no jogo. Estas opções de armazenamento podem ser: *storage crates*, *backpacks*, *reinforced chests*, *armor stands*, *locks*, entre outros.


##Requisitos


###Requisitos do sistema


A versão mais recente do **BetterStorage** foi desenvolvida para o **Minecraft 1.7.10**. Para ser instalado e executado, o *mod* necessita que haja instalada uma versão compatível do respetivo jogo, assim como esteja presente uma versão atualizada do **Java**. É também necessário que se instale uma versão recente do [Minecraft Forge](https://files.minecraftforge.net/) (API de modding para o Minecraft).


###Elicitação de requisitos


Em conversação com a "proprietária" do repositório, [copygirl](https://github.com/copygirl), já durante a elaboração do [primeiro relatório](https://github.com/VascoUP/BetterStorage/blob/master/ESOF-docs/Relat%C3%B3rio-1.md) (não tendo existido nenhum tipo de conversa durante a elaboração do segundo relatório, visto que a mesma e os elementos [Victorious3](https://github.com/Victorious3) e [Thog](https://github.com/Thog) não responderam às perguntas elaboradas pelo grupo), chegou-se à conclusão que o **BetterStorage** é um projeto com uma natureza recreativa e simples, fazendo com que qualquer pessoa/[contribuidor](https://github.com/copygirl/BetterStorage/graphs/contributors) possa sugerir *features* e contribuir para o mesmo.

As sugestões e discussões podem fazer-se tanto através das [*issues*](https://github.com/copygirl/BetterStorage/issues) do repositório do [GitHub do BetterStorage](https://github.com/copygirl/BetterStorage), como através dos *threads* no [fórum do Minecraft](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/wip-mods/1442380-betterstorage-0-13-1-127-wip).


<p align="center">
  <img src="https://github.com/VascoUP/BetterStorage/blob/master/ESOF-docs/resources/ISSUES.png">
</p>


Os contribuidores (*developers*) têm como principal função corrigir [*issues*](https://github.com/copygirl/BetterStorage/issues), principalmente *bugs*, enquanto que a [copygirl](https://github.com/copygirl) e [Victorious3](https://github.com/Victorious3) é que tratam de lançar as novas versões do *mod*, logo são quem contribuem mais para o desenvolvimento deste projeto.

Em relação aos requisitos pré-estabelecidos, não se pode concluir com exatidão a maneira com que estes são criados, pois nunca foram propostas metas de trabalho, visto que não existem [*milestones*](https://github.com/copygirl/BetterStorage/milestones), contudo, cada contribuidor pode entrar em contacto com a [copygirl](https://github.com/copygirl), através das redes sociais da mesma ou através do fórum relativo ao projeto para verificar a possibilidade de trabalhar em novas versões do **BetterStorage**.

Cada pessoa que queira contribuir para este projeto pode fazer um novo [*branch*](https://github.com/copygirl/BetterStorage/branches), podendo-se fazer um [*pull request*](https://github.com/copygirl/BetterStorage/pulls) para, posteriormente, se adicionar o trabalho desenvolvido ao projeto principal. Este método permite estruturar o projeto de forma a que cada contribuidor possa desenvolver novas melhorias ou corrigir possíveis erros sem estar a interferir com o trabalho dos outros. Neste caso, não existem muitos colaboradores, existindo apenas 24, mas mesmo assim, poderiam haver muitos conflitos caso todos os *developers* quisessem trabalhar na “versão principal”, pois assim poderia haver código repetido, ou até código que não se conjuga-se entre si.

Para além disso, a gestora do projeto deixa bem claro no ficheiro [README](https://github.com/VascoUP/BetterStorage/blob/master/README.md), presente no repositório do projeto, que todos os contribuidores devem manter a estrutura do projeto intacta, para não haver mudanças repentinas na estrutura repensada, previamente, pelo "núcleo principal" do **BetterStorage**.

Relativamente às [*issues*](https://github.com/copygirl/BetterStorage/issues), já referidas, estas servem para reportar possíveis problemas, podendo ser uma espécie de lista de melhorias a fazer, uma vez que cada *developer* pode tentar resolver as mesmas, acabando por aperfeiçoar o projeto existente.


###Especificação


Mais uma vez, nenhum processo formal foi utilizado na especificação de requerimentos, visto que não existe nenhum documento **SRC (Software Requirements Specification)**. Este projeto apenas contém um ficheiro [README](https://github.com/VascoUP/BetterStorage/blob/master/README.md) onde está presente uma descrição do projeto e uma breve explicação acerca da preparação do "ambiente de desenvolvimento", com algumas explicações referentes aos requisitos não funcionais do projeto.


###Validação


Apesar de não haverem [*milestones*](https://github.com/copygirl/BetterStorage/milestones) nem *deadlines*, é necessário que haja algum tipo de validação relativamente às contribuições dos colaboradores, para que o seu contributo seja efetivamente benéfico para o projeto.

No **BetterStorage** não existe nenhuma barreira que impeça os contribuidores de desenvolver novas melhorias para o *mod* tendo, somente, que aquando da junção do seu código com o código já existente pedir aprovação, principalmente, à “proprietária” do repositório, para que tal ocorra.

Pode-se, no entanto, obter uma pré-validação das funcionalidades a implementar através do [fórum do Minecraft](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/wip-mods/1442380-betterstorage-0-13-1-127-wip), onde cada contribuidor pode expor as suas ideias e sugerir novos requisitos para o projeto, podendo obter algum *feedback* dos outros *developers* e da principal responsável pelo projeto. Este não é um método que assegure uma validação certa das melhorias que estão a ser propostas, mas é uma forma de se saber se a ideia de um contribuidor tem fundamentos e se causa impacto no **BetterStorage**.

Depois de serem executadas as melhorias e funções que o contribuidor se propôs a implementar, este pode fazer um *pull request* para que a “gerente” do repositório, ou alguém mais envolvido no projeto, como a [Victorious3](https://github.com/Victorious3), possam verificar as tais melhorias e de seguida, caso seja validado o trabalho do *developer* se possa fazer *merge* do *branch* que estava a ser usado pelo mesmo (como já referido no [relatório anterior](https://github.com/VascoUP/BetterStorage/blob/master/ESOF-docs/Relat%C3%B3rio-1.md), cada contribuidor cria um novo *branch* para conseguir trabalhar sem interferir com o trabalho dos outros).


<p align="center">
  <img src="https://github.com/VascoUP/BetterStorage/blob/master/ESOF-docs/resources/PULL.png">
</p>


Assim, o trabalho efetuado fica acresecentado à versão anterior do projeto, criando uma nova versão do mesmo que pode, de seguida, fazer parte das [*releases*](https://github.com/copygirl/BetterStorage/releases) publicadas.


##Casos de Uso


Para determinar os requisitos a aplicar numa aplicação é necessário estar a par do tipo de utilização. No nosso caso, o desenvolvimento de um *mod*, apenas afeta, ligeiramente, o executável do jogo **Minecraft** em pequenos parâmetros:


<p align="center">
  <img src="https://github.com/VascoUP/BetterStorage/blob/master/ESOF-docs/resources/Use%20Case%20Model.png">
</p>


Por outro lado, já dentro do jogo, o *mod* permite acrescentar ao mesmo novos tipos de armazenamento como: *storage crates*, *backpacks*, *reinforced chests*, *armor stands*, *locks*, entre outros.


<p align="center">
  <img src="https://github.com/VascoUP/BetterStorage/blob/master/ESOF-docs/resources/use%20case%20Jogo.png">
</p>


##Domain Model


<p align="center">
  <img src="https://github.com/VascoUP/BetterStorage/blob/master/ESOF-docs/resources/DOMAIN_MODEL.png">
</p>


##Análise Crítica


Em primeiro lugar, tem-se que ter em conta que este projeto tem apenas [20 contribuidores](https://github.com/copygirl/BetterStorage/graphs/contributors), sendo que apenas dois 2 deles é que representam o “núcleo principal” de desenvolvimento do mesmo. Para além disso, tem-se que ter em conta, igualmente, que  o **BetterStorage** não contém quaisquer [*milestones*](https://github.com/copygirl/BetterStorage/milestones) ou *deadlines* para a implementação de novas melhorias, como já referido acima. 

Dito isto, o grupo considerou que a abordagem adotada é de certa forma boa em alguns aspetos, tendo no entanto algumas desvantagens.

Uma das vantagens recai sobre o facto de se centralizar as decisões a serem tomadas, ou seja, as decisões acerca da aceitação dos *pull requests*, numa única pessoa, a [copygirl](https://github.com/copygirl), visto que permite que essa pessoa possa gerir por completo o projeto, estando a par de todas as modificações executadas, sabendo qual o estado de evolução do mesmo.

Por outro lado, o facto de qualquer contribuidor ter a possibilidade de reportar [*issues*](https://github.com/copygirl/BetterStorage/issues) pode ser uma vantagem e ao mesmo tempo uma desvantagem. Primeiramente, pode ser um benefício, devido a permitir que se possa ter mais consciência dos problemas encontrados no projeto, uma vez que poderá haver uma maior reportação de [*issues*](https://github.com/copygirl/BetterStorage/issues) a resolver. Este facto é, também, uma desvantagem, pois assim qualquer pessoa pode reportar um problema e isso pode atrasar o desenvolvimento do projeto, visto que depois há a necessidade de verificar a veracidade das [*issues*](https://github.com/copygirl/BetterStorage/issues) assinaladas, sendo que, essencialmente, é a gestora do repositório que exerce essa função.

Para além disso, o facto de não existirem [*milestones*](https://github.com/copygirl/BetterStorage/milestones) ou *deadlines* pode complicar a execução do trabalho, pois pode originar algum desleixo e desmotivação dos contribuidores do mesmo, assim como um prolongamento do tempo de execução das tarefas a que se propuseram.

Em suma, pode-se concluir que a abordagem utilizada é boa, mas não é perfeita, pois existem desvantagens a ter em conta na mesma.


##Links Externos


- Fórum do projeto: http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/wip-mods/1442380-betterstorage-0-13-1-127-wip


##Contribuição do Grupo


- Bruno Santos: 25%;
- Sara Fernandes: 25%;
- Vasco Pereira: 25%;
- Vasco Ribeiro: 25%;


##Identificação do Grupo


####Grupo 05, Turma 03:


-	Bruno Santos (up201402962@fe.up.pt)
-	Sara Fernandes (up201405955@fe.up.pt)
-	Vasco Pereira (up201403485@fe.up.pt)
-	Vasco Ribeiro (up201402723@fe.up.pt)
