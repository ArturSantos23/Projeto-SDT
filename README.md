# Sprint 6 (Concluído :white_check_mark:)

## Descrição Sprint 6

No âmbito da unidade curricular de Sistemas Distribuídos, foi pedido que fosse implementada uma aplicação em Java que tem como principal objetivo, fazer a distribuição da carga imposta pelos diversos pedidos de clientes.
Para que esta distribuição de carga fosse possível, foi implementado um balanceador que distribui os pedidos pelos processadores existentes. Estes pedidos transportam scripts que irão ser executados nos processadores sobre ficheiros previamente armazenados pelo sistema.
Para que estes ficheiros sejam armazenados no sistema, foi implementada uma camada de storage.
Por fim, foi implementado um coordenador, que irá recolher dados sobre os processadores e enviá-los ao balanceador, para que este consiga fazer uma melhor distribuição de carga.
A comunicação entre as diferentes camadas é essencialmente feita por Remote Method Invocation (RMI) e por Multicast.
A metodologia adotada foi SCRUM, ou seja, o projeto foi realizado ao longo de vários sprints, que culminaram neste trabalho final.

## Arquitetura do sistema em UML: [Sprint 6 UML](https://raw.githubusercontent.com/ArturSantos23/Projeto-SDT/Sprint-6/UML.svg)
