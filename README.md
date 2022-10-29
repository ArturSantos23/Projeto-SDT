# Sprint 1 (Concluido :white_check_mark:)

## Descrição Sprint 1

O cliente submete um pedido de armazenamento de dados no processo líder da camada de storage, e recebe um identificador para os dados (UUID).

## Como testar:
No ficheiro Client.java (Localizado em Client/src) alterar o seguinte código:
- Linha 19: Alterar o pathname, ou seja, a localização para o ficheiro guardado localmente (atenção que no caso de se executar o cliente numa máquina Windows, as subpastas devem ser separadas por duas "\\")
- Linha 24: Alterar o nome do ficheiro (fileName) em `FileData(fileID, fileName, fileBase64)`
- Linha 26: Alterar o nome do ficheiro (fileName) em `l.getFileID(fileName)`
