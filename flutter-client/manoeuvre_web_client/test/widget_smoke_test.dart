import 'package:flutter_test/flutter_test.dart';
import 'package:manoeuvre_web_client/main.dart';

void main() {
  testWidgets('Manoeuvre app renders core panels', (tester) async {
    await tester.pumpWidget(const ManoeuvreApp());
    await tester.pumpAndSettle();

    expect(find.text('Connection'), findsNothing);
    expect(find.text('Login'), findsNWidgets(2));
    expect(find.text('Create Room'), findsNWidgets(2));
    expect(find.text('Available Rooms'), findsOneWidget);
    expect(find.text('Current Session'), findsOneWidget);
    expect(find.text('Nation'), findsOneWidget);
    expect(find.text('Game'), findsOneWidget);
    expect(find.text('Event Log'), findsOneWidget);
  });
}
