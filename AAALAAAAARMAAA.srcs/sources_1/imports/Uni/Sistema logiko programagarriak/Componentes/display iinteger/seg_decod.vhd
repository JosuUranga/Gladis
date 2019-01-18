----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 03.10.2018 10:10:03
-- Design Name: 
-- Module Name: seg_decod - Behavioral
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


entity seg_decod is
    Port ( A : in integer;-- 5 biteko bektorea izango dugu sarrera bezala
           seg : out STD_LOGIC_VECTOR (6 downto 0));--7 segmentu direnez 7 biteko bektorea izango dugu irteera bezala
end seg_decod;

architecture Behavioral of seg_decod is

begin

with A select--with.. select instrukzioa erabilita sarrera bakoitzerako irteera jakin bat definitzen dugu, horrela 0000 denean 0 bat agertuko da
             --displayan, 0001 denean 1ekoa, 0010 denean 2koa,0011 denean 3koa, etab. zenbakia 9 baino haundiagoa denean ezin izango dugu irudikatu
             -- digito batekin,, beraz - agertuko da pantailan ( dena den batuketa egiten jarraituko da, baina ez da emaitza pantailan agertuko)
    seg<=  "1000000" when 0,
           "1111001" when 1,
           "0100100" when 2,
           "0110000" when 3,
           "0011001" when 4,
           "0010010" when 5,
           "0000010" when 6,
           "1111000" when 7,
           "0000000" when 8,
           "0010000" when 9,
           "0111111" when others; --beste kasuetan "-"
end Behavioral;
