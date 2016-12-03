# Relatório 4

## Índice
1. [Introdução](#introduction)
2. [Grau de Testabilidade](#testability)
  1. [Contrabilidade](#controllability)
  2. [Observabilidade](#observability)
  3. [Isolabilidade](#isolateability)
  4. [Separação de preocupações](#preocupations)
  5. [Percetibilidade](#understand)
  6. [Heterogeneidade](#heterogeneidade)
3. [Estatísticas de Teste](#statistics)
4. [Bugs](#bugs)
  1. [Identificação do bug a tratar](#bugid)
  2. [Resolução do bug](#bugres)
5. [Conclusão](#conclusion)
6. [Links Externos](#links)
7. [Contribuições](#contribuitions)
8. [Identificação do Grupo](#idgrupe)


<a name="introduction">
## Introdução


Este relatório irá-se focar na **verificação** e **validação** do *software* desenvolvido no projeto **BetterStorage**.

O processo de **verificação** e **validação** permite confirmar que o *software* desenvolvido cumpre com todas as especificações e ideais que o próprio cliente requeriu. Estes dois componentes interligam-se, como se pode ver de seguida:


<p align="center">
  <img src="https://github.com/VascoUP/BetterStorage/blob/master/ESOF-docs/resources/VerificationValidation.jpg">
</p>

O componente de **verificação** tenta garantir, principalmente, através de análises, que os produtos do trabalho e o próprio trabalho final são "bem construídos", isto é, que estão em conformidade com os seus requisitos especificados.

Já o componente de **validação** tenta garantir, através de testes, que o produto final cumpre com a sua intenção. Este pode, igualmente, ser aplicado a produtos intermédios, do respetivo trabalho, como uma espécie de previsão relativa a como é que o produto final irá satisfazer as necessidades do usuário.

O **BetterStorage**, por ser um projeto "livre" onde qualquer um pode ser um contribuidor, podendo participar no desenvolvimento do mesmo, sofre de alguns problemas de testabilidade. Este não contém um módulo relativo a testes, uma vez que tanto a *developer* principal, [copygirl](http://github.com/copygirl) como os restantes contribuidores preferiram criar código e testá-lo manualmente, jogando o jogo *Minecraft*, verificando aí a possível existência de erros.

Apesar de não existirem testes no **BetterStorage**, decidimos supôr como seriam os resultados obtidos em relação ao *grau de testabilidade* e às suas respetivas secções.

Neste relatório, portanto,  explorar-se-á o **grau de testabilidade** do *software*, analisando a **controlabilidade** dos componentes, a **observabilidade** e **isolabilidade** dos mesmos, assim como o **grau de separação de preocupações**, de **percetibilidade** e **heterogeneidade** das tecnologias utilizadas.

Para além disso, vão ser ainda apresentadas algumas estatísticas pertinentes, relacionadas com a verificação e validação do *software*.
Por fim, será apresentado o *bug* selecionado pelo grupo, para ser resolvido, assim como todos os passos efetuados para a sua resolução.


<a name="testability">
## Grau de Testabilidade


A análise referente ao **grau de testabilidade** de um projeto é bastante importante, visto que assim é verificado se os testes têm uma complexidade que permite que todos os componentes envolvidos no *software* sejam testados. Caso o **grau de testabilidade** de um componente seja elevado, significa que é mais fácil encontrar *bugs* no mesmo, podendo-os corrigir com maior facilidade, igualmente.

O **BetterStorage**, como já referido, não contém nenhum tipo de testes, o que faz com que uma vez que o grupo está a supôr certos resultados que poderiam ocorrer caso existisse um  módulo de testes, não se possa analisar o **grau de testabilidade** do projeto de forma concreta e 100% correta. Para tal, iremos identificar e explicar o que ocorre em cada secção do **grau de testabilidade**, referindo o que devia de acontecer caso o **BetterStorage** tivesse testes unitários.

O **grau de testabilidade**, como é indicado, é dividido em diferentes secções, como:
- Controlabilidade
- Observabilidade
- Isolabilidade
- Separação de preocupações
- Compreensibilidade
-	Heterogeneidade


<a name="controllability">
### Contrabilidade

A **controlabilidade** é uma fase em que é possível controlar o estado do componente que irá ser testado (*CUT - Component Under Test*) em conformidade com o teste respetivo.

Uma vez que o **BetterStorage** não contém testes unitários, não podemos falar concretamente dos módulos existentes e como estes se comportariam ao serem testados. 

Caso este projeto tivesse testes unitários, provavelmente, a **controlabilidade** dos componentes do módulo principal do **BetterStorge** seria reduzida, uma vez que estes dependem de outros módulos. Já a **controlabilidade** dentro de cada módulo seria maior, visto que a maior partes dos componentes de cada módulo interagem com os restantes componentes do módulo respetivo. 

Deste modo, a **controlabilidade** mais elevada dos componentes dos módulos torna mais fácil a execução de testes existentes, em relação a alguns componentes do módulo principal. Contudo, componentes mais "interiores" do módulo principal poderão apresentar a mesma facilidade (quando considerada apenas a **controlabilidade**), visto que possuem interações mais limitadas e funcionalidades mais concentradas.

Podemos, assim, afirmar que quanto maior for a profundidade do componente, em relação à sua posição no código, maior será a **controlabilidade** do mesmo.

<a name="observability">
### Observabilidade

<a name="isolateability">
### Isolabilidade

<a name="preocupations">
### Separação de Preocupações

<a name="understand">
### Percetibilidade

<a name="heterogeinidade">
### Heterogeneidade

<a name="statistics">
## Estatísticas de Teste

<a name="bugs">
## Bugs

<a name="bugid">
### Identificação do bug a tratar
<a name="bugres">
### Resolução do bug

<a name="conclusion">
## Conclusão

<a name="links">
## Links Externos

<a name="contribuitions">
## Contribuições

<a name="idgrupe">
## Identificação do Grupo
