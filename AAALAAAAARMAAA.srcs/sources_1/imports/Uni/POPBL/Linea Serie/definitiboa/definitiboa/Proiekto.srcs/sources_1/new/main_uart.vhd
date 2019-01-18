
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity main_uart is
Port ( serial_in : in std_logic;            
       clk : in std_logic;
       data_out : out STD_LOGIC_VECTOR(7 downto 0);
       data_in : in STD_LOGIC_VECTOR(7 downto 0);
       data_strobe : out STD_LOGIC;
       en_16_x_baud: in  std_logic;
       serial_out: out std_logic;
       tx_complete: out std_logic;
       send_character : in std_logic);
end main_uart;

architecture Behavioral of main_uart is



signal baud_count: integer range 0 to 65:=0;--raising trigger bat frekuentzia aldatzeko
-- frekuentzia aldatutako seinalea
signal cont: integer:=0;-- kontadorea murrizteko frekuentzia zenbaki bakoitza x aldiz

component kcuart_rx is
     Port (   serial_in : in std_logic;  
              data_out : out std_logic_vector(7 downto 0);
              data_strobe : out std_logic;
              en_16_x_baud : in std_logic;
              clk : in std_logic);
end component;

component kcuart_tx
Port (        data_in : in std_logic_vector(7 downto 0);
           send_character : in std_logic;
             en_16_x_baud : in std_logic;
               serial_out : out std_logic;
              Tx_complete : out std_logic;
                      clk : in std_logic);
end component;



begin
tb_kcuartrx: kcuart_rx port map (serial_in => serial_in  , data_out=> data_out ,  data_strobe=> data_strobe ,en_16_x_baud=>en_16_x_baud  , clk=>clk );
tb_kcuartrt: kcuart_tx port map (data_in=> data_in , send_character=> send_character,
en_16_x_baud=>en_16_x_baud, serial_out=> serial_out,   Tx_complete=>Tx_complete, clk=>clk);
   

end Behavioral;
