# Sistema de Tickets de Suporte

Um sistema completo de gerenciamento de tickets de suporte desenvolvido com HTML, CSS, JavaScript e Java.

## 🚀 Funcionalidades Implementadas

### ✅ **Core do Sistema**
- **Backend Java Completo** com classes para Ticket, Usuario, Comentario e Categoria
- **Interface Web Responsiva** com navegação intuitiva
- **Dashboard Interativo** com estatísticas em tempo real
- **Sistema de Autenticação** com diferentes perfis de usuário

### ✅ **Gestão de Tickets**
- ✅ Criação de tickets com validação completa
- ✅ Listagem com filtros por status, prioridade e categoria
- ✅ Visualização detalhada de tickets
- ✅ Sistema de comentários e histórico
- ✅ Alteração de status (Aberto → Em Andamento → Resolvido → Fechado)
- ✅ Atribuição de responsáveis
- ✅ Sistema de tags para classificação

### ✅ **Dashboard e Relatórios**
- ✅ Estatísticas em tempo real
- ✅ Gráficos de tickets por status e prioridade
- ✅ Métricas avançadas (tempo médio de resolução, tickets vencidos)
- ✅ Lista de tickets recentes com filtros rápidos

### ✅ **Sistema de Usuários**
- ✅ Três perfis: Administrador, Técnico e Usuário
- ✅ Controle de permissões por perfil
- ✅ Gestão de usuários e departamentos

### ✅ **Funcionalidades Avançadas**
- ✅ Sistema de notificações com alertas
- ✅ Validação de formulários em tempo real
- ✅ Interface responsiva para mobile
- ✅ Sistema de busca e filtros avançados
- ✅ Paginação de resultados

## 🏗️ Arquitetura do Sistema

### **Frontend**
- **HTML5**: Estrutura semântica e acessível
- **CSS3**: Design responsivo com variáveis CSS
- **JavaScript ES6+**: Lógica de negócio e interatividade
- **Chart.js**: Gráficos e visualizações

### **Backend**
- **Java**: Classes de modelo e lógica de negócio
- **JSON**: Armazenamento de dados local
- **LocalStorage**: Persistência no navegador

### **Estrutura de Arquivos**
```
Sistema de Tickets/
├── Sistema.html              # Interface principal
├── Sistema.java              # Backend Java
├── css/
│   ├── styles.css           # Estilos principais
│   └── dashboard.css        # Estilos do dashboard
├── js/
│   ├── dados.js            # Dados iniciais
│   ├── utils.js            # Utilitários
│   ├── main.js             # JavaScript principal
│   ├── tickets.js          # Gestão de tickets
│   ├── dashboard.js        # Dashboard
│   └── usuarios.js         # Gestão de usuários
├── data/
│   ├── tickets.json        # Dados dos tickets
│   ├── usuarios.json       # Dados dos usuários
│   ├── categorias.json     # Dados das categorias
│   └── configuracoes.json  # Configurações
└── docs/
    ├── arquitetura-sistema-tickets.md
    └── diagrama-sistema.md
```

## 🚀 Como Usar

### **Instalação**
1. Clone ou baixe todos os arquivos
2. Abra o arquivo `Sistema.html` em um navegador moderno
3. O sistema carregará automaticamente com dados de exemplo

### **Usuários Padrão**
- **Admin**: admin@sistema.com (senha: admin123)
- **Técnico**: tecnico@sistema.com (senha: tecnico123)
- **Usuário**: joao.silva@empresa.com (senha: usuario123)

### **Navegação**
- **Dashboard**: Visão geral com estatísticas
- **Tickets**: Listagem e gestão de tickets
- **Novo Ticket**: Formulário de criação
- **Usuários**: Gestão de usuários (Admin)
- **Categorias**: Gestão de categorias (Admin)

## 📊 Funcionalidades por Perfil

### **👤 Usuário Comum**
- Criar tickets próprios
- Visualizar tickets próprios
- Comentar em tickets próprios
- Acompanhar status dos tickets

### **🔧 Técnico**
- Todas as funcionalidades de usuário
- Visualizar todos os tickets
- Assumir e resolver tickets
- Alterar status dos tickets
- Comentar em qualquer ticket

### **👑 Administrador**
- Todas as funcionalidades de técnico
- Gerenciar usuários e perfis
- Gerenciar categorias
- Configurar sistema
- Acessar relatórios completos
- Exportar dados

## 🎨 Interface

### **Design Responsivo**
- Layout adaptável para desktop, tablet e mobile
- Cores consistentes com variáveis CSS
- Ícones Font Awesome para melhor UX
- Animações suaves e transições

### **Componentes Principais**
- **Header**: Navegação e informações do usuário
- **Sidebar**: Menu principal com contadores
- **Dashboard**: Cards de estatísticas e gráficos
- **Tabelas**: Listagem de tickets com filtros
- **Modais**: Visualização detalhada de tickets
- **Formulários**: Criação e edição com validação

## 🔧 Tecnologias Utilizadas

- **HTML5**: Estrutura e semântica
- **CSS3**: Estilização e responsividade
- **JavaScript ES6+**: Lógica e interatividade
- **Java**: Backend e modelos de dados
- **Chart.js**: Gráficos e visualizações
- **Font Awesome**: Ícones
- **JSON**: Armazenamento de dados

## 📈 Métricas e Estatísticas

O sistema calcula automaticamente:
- Total de tickets por status
- Tempo médio de resolução
- Tickets críticos em aberto
- Tickets vencidos por SLA
- Distribuição por categoria
- Performance por técnico

## 🔒 Segurança

- Validação de dados no frontend e backend
- Controle de acesso por perfil
- Sanitização de entradas
- Logs de auditoria
- Prevenção contra XSS

## 🚀 Próximas Funcionalidades

### **Em Desenvolvimento**
- Sistema de anexos para tickets
- Relatórios avançados com exportação
- Integração com email
- API REST completa
- Sistema de SLA automático

### **Planejado**
- Integração com Active Directory
- Aplicativo mobile
- Chatbot para suporte
- Integração com ferramentas externas

## 📝 Changelog

### **v1.0.0** - 2025-01-01
- ✅ Sistema base implementado
- ✅ Dashboard funcional
- ✅ CRUD completo de tickets
- ✅ Sistema de usuários
- ✅ Interface responsiva
- ✅ Validações e notificações

## 🤝 Contribuição

Para contribuir com o projeto:
1. Faça um fork do repositório
2. Crie uma branch para sua feature
3. Implemente as mudanças
4. Teste thoroughly
5. Submeta um pull request

## 📞 Suporte

Para suporte técnico ou dúvidas:
- Abra um ticket no próprio sistema
- Consulte a documentação técnica
- Verifique os logs do navegador

## 📄 Licença

Este projeto está sob licença MIT. Veja o arquivo LICENSE para mais detalhes.

---

**Desenvolvido com ❤️ para facilitar o gerenciamento de suporte técnico**