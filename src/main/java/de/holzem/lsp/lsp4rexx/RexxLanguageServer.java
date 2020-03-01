/**
 * Copyright 2020 Markus Holzem
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.holzem.lsp.lsp4rexx;

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CompletionOptions;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

public class RexxLanguageServer implements LanguageServer, LanguageClientAware {
	private final TextDocumentService textDocumentService;
	private final WorkspaceService workspaceService;
	private LanguageClient languageClient;
	private int errorCode = 1;

	public RexxLanguageServer() {
		this.textDocumentService = new RexxTextDocumentService();
		this.workspaceService = new RexxWorkspaceService();
	}

	@Override
	public CompletableFuture<InitializeResult> initialize(final InitializeParams initializeParams)
	{
		// Initialize the InitializeResult for this LS.
		final InitializeResult initializeResult = new InitializeResult(new ServerCapabilities());

		// Set the capabilities of the LS to inform the client.
		initializeResult.getCapabilities().setTextDocumentSync(TextDocumentSyncKind.Full);
		final CompletionOptions completionOptions = new CompletionOptions();
		initializeResult.getCapabilities().setCompletionProvider(completionOptions);
		return CompletableFuture.supplyAsync(() -> initializeResult);
	}

	@Override
	public CompletableFuture<Object> shutdown()
	{
		// If shutdown request comes from client, set the error code to 0.
		errorCode = 0;
		return null;
	}

	@Override
	public void exit()
	{
		// Kill the LS on exit request from client.
		System.exit(errorCode);
	}

	@Override
	public TextDocumentService getTextDocumentService()
	{
		// Return the endpoint for language features.
		return this.textDocumentService;
	}

	@Override
	public WorkspaceService getWorkspaceService()
	{
		// Return the endpoint for workspace functionality.
		return this.workspaceService;
	}

	@Override
	public void connect(final LanguageClient languageClient)
	{
		// Get the client which started this LS.
		this.languageClient = languageClient;
	}
}