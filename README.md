<div align="center">
  <img src="https://i.imgur.com/n4GvGl0.png" width="300" alt="Logo do L2JPremium">

  <h1>L2JPremium · High Five</h1>

  <strong>Servidor Lineage II High Five em Java, com painéis visuais para administrar o Auth Server e o Game Server.</strong>

  <a href="#downloads"><img src="https://img.shields.io/badge/DOWNLOADS-FF7A00?style=for-the-badge&logo=files&logoColor=white" alt="Downloads"></a>
  <a href="#instalacao"><img src="https://img.shields.io/badge/INSTALAÇÃO-F5C451?style=for-the-badge&logo=rocket&logoColor=151821" alt="Instalação"></a>
  <a href="#paineis"><img src="https://img.shields.io/badge/PAINÉIS-58A6FF?style=for-the-badge&logo=windows&logoColor=white" alt="Painéis"></a>

  <br>

  <img src="https://img.shields.io/badge/Chronicle-High%20Five-FF7A00?style=flat-square" alt="Chronicle High Five">
  <img src="https://img.shields.io/badge/Java-JDK%2011-F5C451?style=flat-square&logo=openjdk&logoColor=151821" alt="Java JDK 11">
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white" alt="MySQL 8.0">
  <img src="https://img.shields.io/badge/Windows-10%20%7C%2011-58A6FF?style=flat-square&logo=windows11&logoColor=white" alt="Windows 10 ou 11">
  <a href="LICENSE"><img src="https://img.shields.io/badge/Licença-GPL--3.0-2EA44F?style=flat-square" alt="Licença GPL-3.0"></a>

  <br><br>

  <a href="#visao-geral">Visão geral</a> · <a href="#downloads">Downloads</a> · <a href="#instalacao">Instalação</a> · <a href="#banco-de-dados">Banco de dados</a> · <a href="#configuracao">Configuração</a> · <a href="#paineis">Painéis</a>
</div>

---

<a id="visao-geral"></a>

## 🔥 Visão geral

O **L2JPremium** é uma base de servidor **Lineage II High Five** preparada para desenvolvimento, customização e execução no Windows. O projeto reúne Auth Server, Game Server, scripts, banco de dados, ferramentas para edição da System e painéis administrativos em uma única estrutura.

| ⚡ Execução | 🛡️ Administração | 🧰 Desenvolvimento |
|:---|:---|:---|
| Auth e Game Server separados | Status e logs em tempo real | Código-fonte Java 11 |
| Inicialização silenciosa por VBS | Reinício e desligamento pelo painel | Compilação com Apache Ant |
| Configurações organizadas por módulo | Monitoramento de portas e conexões | Scripts e datapack separados |

> [!IMPORTANT]
> Este repositório contém os arquivos do **servidor**. O Cliente High Five e a System personalizada terão downloads próprios assim que os pacotes forem publicados.

<a id="downloads"></a>

## 📥 Central de downloads

### Requisitos do servidor

| Componente | Versão | Download |
|:---|:---:|:---:|
| **Java JDK** — necessário para compilar e executar o servidor | 11 | [![Baixar Java 11](https://img.shields.io/badge/BAIXAR-JAVA%2011-FF7A00?style=for-the-badge&logo=openjdk&logoColor=white)](https://mega.nz/file/V7tj1arS#OKWaTzaCqYK0m3iMmR0kW3TddfAJoiu8a20kOFEKShk) |
| **MySQL Community Server** — banco de dados do projeto | 8.0.42 | [![Baixar MySQL](https://img.shields.io/badge/BAIXAR-MYSQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mediafire.com/file/fj0gbi0bu1easbt/mysql-installer-community-8.0.42.0.msi/file) |
| **Navicat Premium** — administração e restauração do banco | 15.0.17 | [![Baixar Navicat](https://img.shields.io/badge/BAIXAR-NAVICAT-F5C451?style=for-the-badge&logo=databricks&logoColor=151821)](https://www.mediafire.com/file/i0u6721vkirc7d6/Navicat_Premium_15.0.17.rar/file) |

<div align="center">
  <sub>Outra plataforma? Acesse a <a href="https://adoptium.net/temurin/releases/?version=11">página oficial do Eclipse Temurin 11</a>.</sub>
</div>

### Arquivos para jogar

| Pacote | Situação | Download |
|:---|:---:|:---:|
| **Cliente Lineage II High Five** | ATIVO | [![Cliente High Five](https://img.shields.io/badge/CLIENTE%20LINK-6E7681?style=for-the-badge&logo=gamejolt&logoColor=white)](https://www.mediafire.com/file/ie786fisx26bhmr/Imperium.rar/file) |
| **System L2JPremium** | Em breve | [![System do Servidor](https://img.shields.io/badge/SYSTEM%20LINK-6E7681?style=for-the-badge&logo=windows&logoColor=white)](#) |

> [!NOTE]
> Cliente Full Não Acompanha System você deve baixar ambos.

### Ferramenta para editar a System

O **L2FileEdit High Five** acompanha o repositório e permite editar arquivos da pasta `System`, como textos, nomes e outras informações do cliente.

<div align="center">
  <a href="L2FileEdit/High%20Five%20FileEdit.rar"><img src="https://img.shields.io/badge/ABRIR-L2FILEEDIT-9B6DFF?style=for-the-badge&logo=files&logoColor=white" alt="Abrir L2FileEdit"></a>
</div>

> [!WARNING]
> Faça uma cópia de segurança da `System` antes de editar qualquer arquivo. Utilize ferramentas e pacotes de terceiros de acordo com suas respectivas licenças.

## 🖥️ Requisitos recomendados

| Item | Desenvolvimento local | Servidor em produção |
|:---|:---|:---|
| Sistema operacional | Windows 10 ou 11 | Windows Server 2019 ou superior |
| Processador | Intel Core i5 / AMD Ryzen 5 | Intel Xeon / AMD Ryzen |
| Memória RAM | 8 GB | 16 GB ou mais |
| Armazenamento | 20 GB livres em SSD | SSD com espaço para logs e backups |
| Java | JDK 11 | JDK 11 |
| Banco de dados | MySQL 8.0 | MySQL 8.0 |
| Ferramentas | Navicat 15, Eclipse e Apache Ant | Navicat 15 |

<a id="instalacao"></a>

## 🚀 Instalação rápida

### 1. Instale os requisitos

Instale o **Java JDK 11**, o **MySQL 8.0** e o **Navicat 15** pelos botões da [Central de downloads](#downloads). Depois, confirme o Java no PowerShell ou Prompt de Comando:

```powershell
java -version
```

O resultado deve indicar a versão `11`.

### 2. Prepare o banco de dados

1. Inicie o serviço do MySQL.
2. No Navicat, crie uma conexão para `localhost`, porta `3306`.
3. Crie ou selecione o banco `l2jpremium`.
4. Use a função **Restore Backup** do Navicat e selecione [`db/backup_navicat_15.nb3`](db/backup_navicat_15.nb3).
5. Confirme que as tabelas foram restauradas antes de iniciar os servidores.

<details>
<summary><strong>Backup alternativo</strong></summary>
<br>

O diretório [`db`](db) também contém `backup_navicat_8.psc`, mantido para compatibilidade com instalações antigas. Para uma instalação nova, prefira o backup do **Navicat 15**.

</details>

### 3. Configure a conexão

Atualize host, porta, nome do banco, usuário e senha nestes arquivos:

| Serviço | Arquivo de configuração |
|:---|:---|
| Auth Server | [`auth/config/loginserver.ini`](auth/config/loginserver.ini) |
| Game Server | [`world/config/server.ini`](world/config/server.ini) |

Use as mesmas credenciais cadastradas no MySQL e evite manter a senha padrão em um ambiente público.

### 4. Inicie os servidores

Execute nesta ordem:

1. [`auth/start_login.vbs`](auth/start_login.vbs) — aguarde o Auth Server ficar online.
2. [`world/start_gameserver.vbs`](world/start_gameserver.vbs) — aguarde o registro no Auth Server.

> [!TIP]
> Os inicializadores usam `javaw` e abrem os painéis sem manter uma janela de console. Em caso de falha, consulte os logs dentro de `auth/log` e `world/log`.

<a id="banco-de-dados"></a>

## 🗄️ Banco de dados

Os backups necessários já estão versionados no diretório [`db`](db):

```text
db/
├── backup_navicat_15.nb3   # backup recomendado
└── backup_navicat_8.psc    # alternativa para versão antiga
```

Configuração padrão esperada pelo projeto:

| Propriedade | Valor local |
|:---|:---|
| Host | `localhost` |
| Porta | `3306` |
| Banco | `l2jpremium` |
| Usuário | definido na instalação |
| Senha | definida na instalação |

<a id="configuracao"></a>

## ⚙️ Configuração

### Rede e portas

| Porta | Serviço | Finalidade |
|:---:|:---|:---|
| `2106` | Auth Server | Conexão dos clientes |
| `9014` | Auth ↔ Game | Registro do Game Server |
| `7777` | Game Server | Conexão com o mundo |
| `3306` | MySQL | Banco de dados |

Para acesso externo, ajuste os endereços em [`auth/config/loginserver.ini`](auth/config/loginserver.ini) e [`world/config/server.ini`](world/config/server.ini), além de liberar somente as portas necessárias no firewall.

### Compilação

Com o JDK 11 e o Apache Ant configurados, execute na raiz do projeto:

```powershell
ant
```

O `build.xml` compila o core e os scripts, gerando `libs/L2JPremium.jar` e `libs/scripts.jar`.

<a id="paineis"></a>

## 🎛️ Painéis de controle

Os dois serviços possuem interface visual com status, métricas, console em tempo real e ações administrativas.

| Auth Server | Game Server |
|:---:|:---:|
| <img src="https://i.imgur.com/BkDFW1Y.png" width="100%" alt="Painel visual do L2JPremium Auth Server"> | <img src="https://i.imgur.com/t1bRYqK.png" width="100%" alt="Painel visual do L2JPremium Game Server"> |
| Clientes, Game Servers e banco de dados | Jogadores, memória, portas e tempo online |

### Comportamento dos controles

| Ação | Resultado |
|:---|:---|
| **Reiniciar** | Encerra a JVM com código `2`; o inicializador abre o serviço novamente |
| **Desligar tudo** | Encerra a JVM com código `0` e não reinicia |
| **Limpar logs** | Limpa a visualização e os arquivos administrados pelo painel |
| **Erro crítico** | Encerra com código `1` e informa a falha |

## 🧭 Estrutura do projeto

```text
L2JPremium/
├── auth/          # Auth Server, configurações e inicializador
├── world/         # Game Server, datapack e configurações
├── java/          # código-fonte principal
├── scripts/       # scripts do servidor
├── libs/          # bibliotecas e arquivos compilados
├── db/            # backups do banco de dados
├── L2FileEdit/    # editor de arquivos da System High Five
└── img/           # identidade visual e capturas dos painéis
```

## 🔐 Boas práticas

- Troque as credenciais padrão antes de publicar o servidor.
- Não exponha a porta `3306` diretamente à internet.
- Mantenha backups regulares do banco, configurações e datapack.
- Libere no firewall apenas as portas usadas pelo servidor.
- Não execute duas instâncias utilizando as mesmas portas.
- Verifique os logs sempre que um serviço encerrar com erro.

## 📜 Licença

Este projeto é distribuído sob a licença **GNU General Public License v3.0**. Consulte o arquivo [`LICENSE`](LICENSE) antes de copiar, modificar ou redistribuir o código.

---

<div align="center">
  <img src="https://i.imgur.com/pzQUdEm.png" width="32" alt="Ícone L2JPremium">
  <br>
  <strong>L2JPremium · High Five</strong>
  <br>
  <sub>Desenvolvimento, estabilidade e administração visual para Lineage II.</sub>
  <br><br>
  <img src="https://img.shields.io/badge/MADE%20WITH-JAVA%2011-FF7A00?style=flat-square&logo=openjdk&logoColor=white" alt="Feito com Java 11">
</div>
