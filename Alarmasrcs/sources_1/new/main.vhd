----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 08.01.2019 15:44:03
-- Design Name: 
-- Module Name: main - Behavioral
-- Project Name: 
-- Target Devices: 
-- Tool Versions: 
-- Description: 
-- 
-- Dependencies: 
-- 
-- Revision:
-- Revision 0.01 - File Created
-- Additional Comments:
-- 
----------------------------------------------------------------------------------


library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

-- Uncomment the following library declaration if using
-- arithmetic functions with Signed or Unsigned values
--use IEEE.NUMERIC_STD.ALL;

-- Uncomment the following library declaration if instantiating
-- any Xilinx leaf cells in this code.
--library UNISIM;
--use UNISIM.VComponents.all;

entity main is
    Port ( clk : in STD_LOGIC;
           reset : in STD_LOGIC;
           data_in: in STD_LOGIC_VECTOR (7 downto 0);
           mandar: in STD_LOGIC;
           serial_in : in std_logic;
           serial_out : out std_logic;
           led : out STD_LOGIC_VECTOR (15 downto 0));
           
           
end main;

architecture Behavioral of main is

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
type estados is (init,esperando, mandar_informacion, recibir_informacion);
signal actual, proximo : estados;

signal bidali,onartu:std_logic;
signal blink,led_signal : std_logic_vector (15 downto 0);

signal rx,rx_jaso: std_logic_vector (7 downto 0);
--rx ==se utilizara para gestionar la informacion que llege de data_out

--rx_jaso == copia de rx, para gestionar los leds, evitamos algunos fallo que daba utilizar rx directamente
signal cont,cont1 :integer:=0;


signal tx_complete: std_logic ; 
-- indica que la informacion se a enviado
signal data_strobe: std_logic:='0'; 
--Indica que hay informacion pendiente para leer

signal send_character :std_logic;
-- cuando send character es 1 se envia la informacion


signal baud_count: integer range 0 to 650:=0;--para conseguir el timing de los macros
signal en_16_x_baud:  std_logic;
--seinal con la frecuencia que necesitamos para enviar 9600 bits por segundo teniendo en cuenta el clock de 100.000.0000Hz


begin

tb_kcuartrx: kcuart_rx port map (serial_in => serial_in  , data_out=> rx ,  data_strobe=> data_strobe ,en_16_x_baud=>en_16_x_baud  , clk=>clk );
tb_kcuartrt: kcuart_tx port map (data_in=> data_in , send_character=> send_character,
en_16_x_baud=>en_16_x_baud, serial_out=> serial_out,   Tx_complete=>Tx_complete, clk=>clk);

sek: process (clk,reset)
begin
    if reset ='1' then
        actual<= init;
    elsif clk'event and clk='1' then
        actual<=proximo;
    end if;
end process;
baud_timer: process(clk)
begin 
if clk'event and clk='1' then
    if baud_count >= 650 then
        baud_count <= 0;
        en_16_x_baud <= '1';
        
    else
        baud_count <= baud_count + 1;
        en_16_x_baud <= '0';
    end if;
end if;
end process baud_timer;

comb: process (actual,data_strobe,tx_complete,rx,rx_jaso)-- al definir el proceso definimos la lista de sensibilidad, es decir la señales que escuchará el proceso.
begin

case actual is
    when init =>        
           proximo<=esperando;
    when esperando => 
         send_character<='0';
        if data_strobe ='1' then
            proximo<=recibir_informacion;
       elsif bidali='1'then
            proximo<=mandar_informacion;
        else
            proximo<=esperando;
                end if;   
                
    when recibir_informacion => 
    send_character<='0';
       rx_jaso<=rx;
       proximo<=esperando;
       
       
    when mandar_informacion => 
               send_character<='1';
             if tx_complete ='1' then
                 proximo<=esperando;
             else
                proximo<=mandar_informacion;
           end if;   
      
end case;
end process comb;   
behin_bidali: process (clk,reset)
begin
    if reset ='1' then
       cont1<=0;
       onartu<='1'; 
    elsif clk'event and clk='1' then
        if actual= esperando then   
            if mandar ='1' and onartu<='1' then
              bidali<='1';
              onartu<='0';
             end if;   
               if cont1 >=150000000 then     
                    cont1<=0; 
                    onartu<='1';
                  else
                    cont1<=cont1+1;  
                    bidali<='0';
                    
                end if;
                else
                    cont1<=0;
          end if; 
    end if;
end process;
parpadeo: process(clk,reset)
begin
    if reset ='1' then 
    
    cont<=0;
    blink<="0000000000000000";
    
    elsif clk'event and clk='1' then
    if cont >=10000000 then 
        blink <= not blink;
        cont<=0;
     else
        cont<= cont+1;
      end if;
      end if;
end process;

leds: process(clk,actual,data_strobe,tx_complete,rx,rx_jaso)
begin
if reset ='1' then
    led_signal<="0000000000000000";
end if;
       
if  rx_jaso = "01000001" then --A
led_signal<="0101010101010101";
elsif rx_jaso = "01000100" then  --D
led_signal<="0000000000000000";
elsif rx_jaso = "01000010" then ---B
led_signal<="1111111111111111";
elsif rx_jaso = "01000011" then ---C
led_signal<="1111111111111111" and blink ;
else  led_signal<="0000000000000000"; --when oters.
end if;
end process;
led<= led_signal;

end Behavioral;
