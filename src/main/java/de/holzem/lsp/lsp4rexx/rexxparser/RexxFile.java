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
package de.holzem.lsp.lsp4rexx.rexxparser;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.lsp4j.jsonrpc.CancelChecker;

import de.holzem.lsp.lsp4rexx.rexxscanner.RexxToken;
import de.holzem.lsp.lsp4rexx.rexxscanner.TokenType;
import lombok.Builder;
import lombok.Value;

/**
 * RexxFile accumulates all information gained from the RexxParser.
 */
@Value
@Builder
public final class RexxFile
{
	private final String uri;
	private final List<RexxToken> tokens;
	private final List<String> variables;
	private final List<String> labels;
	private final CancelChecker cancelChecker;

	public String getText()
	{
		final String fileText = tokens.stream().map(token -> token.getText()).collect(Collectors.joining());
		return fileText;
	}

	public RexxToken getToken(final int pIndex)
	{
		return tokens.get(pIndex);
	}

	public int locateToken(final int pLine, final int pColumn)
	{
		final RexxToken compareToken = new RexxToken(TokenType.SYNTHETIC, "", pLine, pColumn, 0);
		int position = Collections.binarySearch(tokens, compareToken, new RexxTokenPositionComparator());
		if (position < 0) {
			position = tokens.size() - 1;
		}
		return position;
	}

	public int locatePrevToken(final int pLine, final int pColumn)
	{
		int position = locateToken(pLine, pColumn);
		return (position > 0 ? --position : position);
	}

	private static class RexxTokenPositionComparator implements Comparator<RexxToken>
	{
		@Override
		public int compare(final RexxToken pToken1, final RexxToken pToken2)
		{
			if (pToken1.getLine() == pToken2.getLine()) {
				if (pToken1.getType() == TokenType.SYNTHETIC) {
					// pToken1 is compare token
					final int cmp = pToken1.getColumn();
					final int start = pToken2.getColumn();
					final int end = start + pToken2.getText().length();
					if (start <= cmp && cmp < end) {
						return 0;
					}
				} else {
					// pToken2 is compare token
					final int cmp = pToken2.getColumn();
					final int start = pToken1.getColumn();
					final int end = start + pToken1.getText().length();
					if (start <= cmp && cmp < end) {
						return 0;
					}
				}
				return pToken1.getColumn() - pToken2.getColumn();
			}
			return pToken1.getLine() - pToken2.getLine();
		}
	}
}
