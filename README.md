# link-async

A universal client library designed to simplify client development.

Previously, the write a TCP or WebSocket client, you will put logic in
both main thread and link handler, which made it difficult to manage.
link-async provides you a `handler-factory` that generates proper link
handler by given a `IPurgatory` implementation for the style of
protocol you were using.

## Usage

See
[examples](https://github.com/clojure-link/link-async/tree/master/examples/link_async/examples).

## License

Copyright © 2019 Ning Sun and contributors

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
