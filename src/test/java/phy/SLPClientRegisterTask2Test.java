package phy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import core.Protocol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exceptions.IWProtocolException;
import exceptions.RegistrationFailedException;
import slp.SLPProtocol;

class SLPClientRegisterTask2Test {
	
	int clientId = 9999;
	int clientPort = 5550;	
	String clientName = "localhost";
	int switchPort = 4999;
	String switchName = "localhost";
	
	PhyProtocol phyProtocolMock;
	PhyMsg testMsg;

	@BeforeEach
	void setup() {
		PhyConfiguration phyConfig;
		try {
			phyConfig = new PhyConfiguration(InetAddress.getByName(clientName), clientPort, Protocol.proto_id.SLP);
			testMsg = new PhyMsg(phyConfig);
		} catch (UnknownHostException e) {
			fail();
		}
		
		phyProtocolMock = mock(PhyProtocol.class);
	}
	
	@Test
	void TestRegisterSuccessfully() throws IOException, IWProtocolException {
		
		testMsg = (PhyMsg)testMsg.parse("phy 5 slp reg resp ACK");
		
		when(phyProtocolMock.receive(anyInt())).thenReturn(testMsg);
		
		SLPProtocol slpProtocol = new SLPProtocol(clientId, false, phyProtocolMock);
		
		assertDoesNotThrow(()->slpProtocol.register(InetAddress.getByName(switchName), switchPort));
		
		verify(phyProtocolMock, times(1)).receive(2000);
		verify(phyProtocolMock).send(eq("slp reg req " + clientId), any(PhyConfiguration.class));
        
	}

	@Test
	void testRegistrationNotAcknowledged() throws IOException, IWProtocolException {

		testMsg = (PhyMsg)testMsg.parse("phy 5 slp reg resp NAK");
		
		when(phyProtocolMock.receive(anyInt())).thenReturn(testMsg);
		
		SLPProtocol slpProtocol = new SLPProtocol(clientId, false, phyProtocolMock);
		
		assertThrows(RegistrationFailedException.class,
				()->slpProtocol.register(InetAddress.getByName(switchName), switchPort));
		
		verify(phyProtocolMock, times(1)).receive(2000);
		verify(phyProtocolMock).send(eq("slp reg req 9999"), any(PhyConfiguration.class));
        
	}

	
	@Test
	void testRegistrationFailedExceptionIsThrownWhenReceivingAMalformedMessage() throws IOException, IWProtocolException {
		
		PhyMsg corruptedMsg = (PhyMsg)testMsg.parse("phy 5 sp rg resp ACK");
		
		when(phyProtocolMock.receive(anyInt())).thenReturn(corruptedMsg).thenThrow(new SocketTimeoutException());
		
		SLPProtocol slpProtocol = new SLPProtocol(clientId, false, phyProtocolMock);
		
		assertThrows(RegistrationFailedException.class, 
				()->slpProtocol.register(InetAddress.getByName(switchName), switchPort));
	}

	@Test
	void testMessageLoss() throws IOException, IWProtocolException {
		
		when(phyProtocolMock.receive(anyInt())).thenThrow(new SocketTimeoutException());
		
		SLPProtocol slpProtocol = new SLPProtocol(clientId, false, phyProtocolMock);
		
		assertThrows(RegistrationFailedException.class, 
				()->slpProtocol.register(InetAddress.getByName(switchName), switchPort));
	}


}